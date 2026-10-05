package com.bankingsystem.service.impl;

import com.bankingsystem.dto.FeeCalculationDTO;
import com.bankingsystem.dto.TransferRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.exception.AccountFrozenException;
import com.bankingsystem.exception.InsufficientBalanceException;
import com.bankingsystem.exception.InvalidTransactionException;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.BeneficiaryRepository;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.service.SystemSettingService;
import com.bankingsystem.service.TransferService;
import com.bankingsystem.util.GeneratorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final SystemSettingService systemSettingService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public TransferServiceImpl(AccountRepository accountRepository,
                               BeneficiaryRepository beneficiaryRepository,
                               TransactionRepository transactionRepository,
                               SystemSettingService systemSettingService,
                               NotificationService notificationService,
                               AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
        this.systemSettingService = systemSettingService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction transferMoney(User senderUser, TransferRequest request, String ipAddress) {
        // 1. Validate Sender Account
        Account senderAccount = senderUser.getAccount();
        if (senderAccount == null) {
            throw new ResourceNotFoundException("No active account associated with sender.");
        }

        if (senderAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Your account is currently " + senderAccount.getStatus() + ". Transfers are blocked.");
        }

        // 2. Identify and Validate Recipient Account
        String targetAccountNumber = request.getRecipientAccountNumber();
        if (request.getBeneficiaryId() != null && request.getBeneficiaryId() > 0) {
            Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(request.getBeneficiaryId(), senderUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Selected beneficiary not found."));
            targetAccountNumber = beneficiary.getAccountNumber();
        }

        if (targetAccountNumber == null || targetAccountNumber.isBlank()) {
            throw new InvalidTransactionException("Recipient account number is required.");
        }

        targetAccountNumber = targetAccountNumber.trim();

        if (senderAccount.getAccountNumber().equalsIgnoreCase(targetAccountNumber)) {
            throw new InvalidTransactionException("You cannot transfer funds to the same account.");
        }

        final String finalTargetAcc = targetAccountNumber;
        Account receiverAccount = accountRepository.findByAccountNumber(finalTargetAcc)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account " + finalTargetAcc + " not found. Please verify the account number."));

        if (receiverAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Recipient account is currently inactive or frozen. Transfer cannot be completed.");
        }

        // 3. Amount & Limit Validation
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transfer amount must be greater than $0.00.");
        }

        if (senderAccount.getDailyTransferLimit() != null && amount.compareTo(senderAccount.getDailyTransferLimit()) > 0) {
            throw new InvalidTransactionException("Transfer amount exceeds your daily transfer limit of $" + senderAccount.getDailyTransferLimit());
        }

        // 4. Calculate Fee and Tax
        FeeCalculationDTO feeBreakdown = systemSettingService.calculateFeeAndTax(amount);
        BigDecimal totalRequired = feeBreakdown.getTotalDebit();

        if (senderAccount.getBalance().compareTo(totalRequired) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient funds. Total required: $%s (Amount: $%s + Fee: $%s + Tax: $%s). Your balance: $%s",
                            totalRequired, amount, feeBreakdown.getServiceFee(), feeBreakdown.getTax(), senderAccount.getBalance())
            );
        }

        // 5. Debit Sender
        BigDecimal senderNewBalance = senderAccount.getBalance().subtract(totalRequired);
        senderAccount.setBalance(senderNewBalance);
        senderAccount.setAvailableBalance(senderNewBalance);
        accountRepository.save(senderAccount);

        // 6. Credit Receiver
        BigDecimal receiverNewBalance = receiverAccount.getBalance().add(amount);
        receiverAccount.setBalance(receiverNewBalance);
        receiverAccount.setAvailableBalance(receiverNewBalance);
        accountRepository.save(receiverAccount);

        // 7. Create Debit Transaction for Sender
        String referenceNumber = GeneratorUtils.generateTransactionReference();
        String description = (request.getDescription() != null && !request.getDescription().isBlank())
                ? request.getDescription().trim()
                : "Transfer to " + receiverAccount.getAccountNumber() + " (" + request.getTransferType() + ")";

        Transaction senderTx = new Transaction();
        senderTx.setReferenceNumber(referenceNumber);
        senderTx.setAccount(senderAccount);
        senderTx.setSenderAccount(senderAccount.getAccountNumber());
        senderTx.setReceiverAccount(receiverAccount.getAccountNumber());
        senderTx.setType(TransactionType.TRANSFER);
        senderTx.setAmount(amount);
        senderTx.setFee(feeBreakdown.getServiceFee());
        senderTx.setTax(feeBreakdown.getTax());
        senderTx.setTotalAmount(totalRequired);
        senderTx.setDescription(description);
        senderTx.setStatus(TransactionStatus.COMPLETED);
        senderTx.setBalanceAfter(senderNewBalance);
        Transaction savedSenderTx = transactionRepository.save(senderTx);

        // 8. Create Credit Transaction for Receiver
        Transaction receiverTx = new Transaction();
        receiverTx.setReferenceNumber(referenceNumber + "-CR");
        receiverTx.setAccount(receiverAccount);
        receiverTx.setSenderAccount(senderAccount.getAccountNumber());
        receiverTx.setReceiverAccount(receiverAccount.getAccountNumber());
        receiverTx.setType(TransactionType.TRANSFER);
        receiverTx.setAmount(amount);
        receiverTx.setFee(BigDecimal.ZERO);
        receiverTx.setTax(BigDecimal.ZERO);
        receiverTx.setTotalAmount(amount);
        receiverTx.setDescription("Received from " + senderAccount.getAccountNumber() + " (" + request.getTransferType() + ")");
        receiverTx.setStatus(TransactionStatus.COMPLETED);
        receiverTx.setBalanceAfter(receiverNewBalance);
        transactionRepository.save(receiverTx);

        // 9. Notifications
        notificationService.createNotification(senderUser, "Transfer Successful",
                String.format("Transferred $%s to account %s. Total debited (incl. fee/tax): $%s. Ref: %s",
                        amount, receiverAccount.getAccountNumber(), totalRequired, referenceNumber),
                "SUCCESS", "/customer/transactions");

        if (receiverAccount.getCustomer() != null) {
            notificationService.createNotification(receiverAccount.getCustomer(), "Funds Received",
                    String.format("You received $%s from account %s. Ref: %s",
                            amount, senderAccount.getAccountNumber(), referenceNumber),
                    "SUCCESS", "/customer/transactions");
        }

        // 10. Audit Logging
        auditLogService.log(senderUser.getEmail(), AuditAction.TRANSFER,
                String.format("Transferred $%s from %s to %s. Total debited: $%s. Ref: %s",
                        amount, senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), totalRequired, referenceNumber),
                ipAddress, "SUCCESS");

        return savedSenderTx;
    }
}
