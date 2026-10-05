package com.bankingsystem.service.impl;

import com.bankingsystem.dto.DepositRequest;
import com.bankingsystem.dto.WithdrawRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.exception.AccountFrozenException;
import com.bankingsystem.exception.InsufficientBalanceException;
import com.bankingsystem.exception.InvalidTransactionException;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.service.AccountService;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.util.GeneratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              NotificationService notificationService,
                              AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByAccountNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        return accountRepository.findByAccountNumber(accountNumber.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByUser(User user) {
        return accountRepository.findByCustomer(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountByAccountNumberOrThrow(String accountNumber) {
        return findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with number: " + accountNumber));
    }

    @Override
    @Transactional
    public Transaction depositMoney(User user, DepositRequest request, String ipAddress) {
        Account account = user.getAccount();
        if (account == null) {
            throw new ResourceNotFoundException("No linked bank account found for user: " + user.getEmail());
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Account " + account.getAccountNumber() + " is " + account.getStatus() + ". Deposits are restricted.");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Deposit amount must be strictly greater than $0.00.");
        }

        // 1. Update balances
        BigDecimal updatedBalance = account.getBalance().add(amount);
        account.setBalance(updatedBalance);
        account.setAvailableBalance(updatedBalance);
        accountRepository.save(account);

        // 2. Create Transaction
        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(GeneratorUtils.generateTransactionReference());
        transaction.setAccount(account);
        transaction.setSenderAccount(request.getPaymentMethod());
        transaction.setReceiverAccount(account.getAccountNumber());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setTax(BigDecimal.ZERO);
        transaction.setTotalAmount(amount);
        transaction.setDescription(request.getDescription() != null && !request.getDescription().isBlank() ?
                request.getDescription().trim() : "Funds deposit via " + request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(updatedBalance);

        Transaction savedTx = transactionRepository.save(transaction);

        // 3. Audit & Notification
        auditLogService.log(user.getEmail(), AuditAction.DEPOSIT,
                "Deposited $" + amount + " into " + account.getAccountNumber() + " (Ref: " + savedTx.getReferenceNumber() + ")", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Deposit Successful",
                "$" + amount + " has been successfully credited to your account " + account.getAccountNumber() + ". New Balance: $" + updatedBalance, "SUCCESS", "/customer/transactions");

        return savedTx;
    }

    @Override
    @Transactional
    public Transaction withdrawMoney(User user, WithdrawRequest request, String ipAddress) {
        Account account = user.getAccount();
        if (account == null) {
            throw new ResourceNotFoundException("No linked bank account found for user: " + user.getEmail());
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Account " + account.getAccountNumber() + " is " + account.getStatus() + ". Withdrawals are not permitted.");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be strictly greater than $0.00.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds. Your available balance is $" + account.getBalance() + " but you requested $" + amount);
        }

        // 1. Deduct balance
        BigDecimal updatedBalance = account.getBalance().subtract(amount);
        account.setBalance(updatedBalance);
        account.setAvailableBalance(updatedBalance);
        accountRepository.save(account);

        // 2. Create Transaction
        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(GeneratorUtils.generateTransactionReference());
        transaction.setAccount(account);
        transaction.setSenderAccount(account.getAccountNumber());
        transaction.setReceiverAccount(request.getMethod());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setTax(BigDecimal.ZERO);
        transaction.setTotalAmount(amount);
        transaction.setDescription(request.getDescription() != null && !request.getDescription().isBlank() ?
                request.getDescription().trim() : "Cash withdrawal via " + request.getMethod());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(updatedBalance);

        Transaction savedTx = transactionRepository.save(transaction);

        // 3. Audit & Notification
        auditLogService.log(user.getEmail(), AuditAction.WITHDRAWAL,
                "Withdrew $" + amount + " from " + account.getAccountNumber() + " (Ref: " + savedTx.getReferenceNumber() + ")", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Withdrawal Confirmed",
                "$" + amount + " was debited from your account " + account.getAccountNumber() + ". New Balance: $" + updatedBalance, "WARNING", "/customer/transactions");

        return savedTx;
    }

    @Override
    @Transactional
    public void updateAccountStatus(Long accountId, AccountStatus status, String adminEmail, String ipAddress) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setStatus(status);
        accountRepository.save(account);

        AuditAction action = status == AccountStatus.ACTIVE ? AuditAction.ACCOUNT_UNFREEZE : AuditAction.ACCOUNT_FREEZE;
        auditLogService.log(adminEmail, action, "Changed status of account " + account.getAccountNumber() + " to " + status, ipAddress, "SUCCESS");
        
        if (account.getCustomer() != null) {
            notificationService.createNotification(account.getCustomer(), "Account Status Changed",
                    "Your bank account " + account.getAccountNumber() + " status is now " + status,
                    status == AccountStatus.ACTIVE ? "SUCCESS" : "DANGER", "/customer/account");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> searchAccounts(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return accountRepository.searchAccounts(search.trim(), pageable);
        }
        return accountRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(AccountStatus status) {
        return accountRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalActiveBalance() {
        BigDecimal total = accountRepository.sumTotalActiveBalance();
        return total != null ? total : BigDecimal.ZERO;
    }
}
