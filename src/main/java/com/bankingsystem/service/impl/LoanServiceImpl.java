package com.bankingsystem.service.impl;

import com.bankingsystem.dto.LoanApplicationRequest;
import com.bankingsystem.dto.LoanApprovalRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.LoanRepository;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.LoanService;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.util.GeneratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public LoanServiceImpl(LoanRepository loanRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           NotificationService notificationService,
                           AuditLogService auditLogService) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Loan applyForLoan(User user, LoanApplicationRequest request, String ipAddress) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLoanType(request.getLoanType());
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setMonthlyIncome(request.getMonthlyIncome());
        loan.setEmploymentType(request.getEmploymentType());
        loan.setPurpose(request.getPurpose().trim());
        loan.setStatus(LoanStatus.PENDING);

        double rate = request.getLoanType().getDefaultInterestRate();
        loan.setInterestRate(rate);

        BigDecimal emi = calculateEmi(request.getRequestedAmount(), rate, request.getTenureMonths());
        loan.setMonthlyEmi(emi);
        loan.setTotalPayable(emi.multiply(BigDecimal.valueOf(request.getTenureMonths())));

        Loan saved = loanRepository.save(loan);

        auditLogService.log(user.getEmail(), AuditAction.LOAN_APPLIED,
                "Applied for " + request.getLoanType().getDisplayName() + " of $" + request.getRequestedAmount(), ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Loan Application Submitted",
                "Your application for " + request.getLoanType().getDisplayName() + " ($" + request.getRequestedAmount() + ") has been submitted for review.", "INFO", "/customer/loans");

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getLoansForUser(User user) {
        return loanRepository.findByUserOrderByAppliedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Loan> getLoanByIdAndUser(Long id, User user) {
        return loanRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Loan> getAllLoansAdmin(LoanStatus status, String search, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return loanRepository.filterLoansAdmin(status, query, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Loan reviewLoan(LoanApprovalRequest request, String adminEmail, String ipAddress) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + request.getLoanId()));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BankingException("Loan has already been reviewed (Status: " + loan.getStatus() + ").");
        }

        loan.setReviewedAt(LocalDateTime.now());
        loan.setRemarks(request.getRemarks() != null ? request.getRemarks().trim() : "");

        boolean isApprove = "APPROVE".equalsIgnoreCase(request.getDecision());

        if (isApprove) {
            BigDecimal approvedAmount = request.getApprovedAmount() != null ? request.getApprovedAmount() : loan.getRequestedAmount();
            double rate = request.getInterestRate() != null ? request.getInterestRate() : loan.getInterestRate();
            int tenure = request.getTenureMonths() != null ? request.getTenureMonths() : loan.getTenureMonths();

            loan.setApprovedAmount(approvedAmount);
            loan.setInterestRate(rate);
            loan.setTenureMonths(tenure);
            loan.setStatus(LoanStatus.APPROVED);

            BigDecimal emi = calculateEmi(approvedAmount, rate, tenure);
            loan.setMonthlyEmi(emi);
            loan.setTotalPayable(emi.multiply(BigDecimal.valueOf(tenure)));

            // Disburse loan to customer's account
            User customer = loan.getUser();
            Account account = customer.getAccount();
            if (account != null) {
                BigDecimal newBalance = account.getBalance().add(approvedAmount);
                account.setBalance(newBalance);
                account.setAvailableBalance(newBalance);
                accountRepository.save(account);

                // Create Transaction record for loan disbursement
                Transaction tx = new Transaction();
                tx.setReferenceNumber(GeneratorUtils.generateTransactionReference());
                tx.setAccount(account);
                tx.setSenderAccount("Apex Horizon Loan Desk");
                tx.setReceiverAccount(account.getAccountNumber());
                tx.setType(TransactionType.LOAN);
                tx.setAmount(approvedAmount);
                tx.setFee(BigDecimal.ZERO);
                tx.setTax(BigDecimal.ZERO);
                tx.setTotalAmount(approvedAmount);
                tx.setDescription("Loan Disbursement: " + loan.getLoanType().getDisplayName() + " (App ID: #" + loan.getId() + ")");
                tx.setStatus(TransactionStatus.COMPLETED);
                tx.setBalanceAfter(newBalance);
                transactionRepository.save(tx);
            }

            auditLogService.log(adminEmail, AuditAction.LOAN_APPROVED,
                    "Approved loan #" + loan.getId() + " for $" + approvedAmount + " to " + customer.getEmail(), ipAddress, "SUCCESS");
            notificationService.createNotification(customer, "Loan Application Approved! 🎉",
                    "Your " + loan.getLoanType().getDisplayName() + " of $" + approvedAmount + " has been approved and credited to your account.", "SUCCESS", "/customer/loans");
        } else {
            loan.setStatus(LoanStatus.REJECTED);
            User customer = loan.getUser();

            auditLogService.log(adminEmail, AuditAction.LOAN_REJECTED,
                    "Rejected loan application #" + loan.getId() + " for " + customer.getEmail(), ipAddress, "SUCCESS");
            notificationService.createNotification(customer, "Loan Application Status",
                    "Your " + loan.getLoanType().getDisplayName() + " application was not approved. Remarks: " + loan.getRemarks(), "DANGER", "/customer/loans");
        }

        return loanRepository.save(loan);
    }

    @Override
    public BigDecimal calculateEmi(BigDecimal principal, double annualInterestRate, int tenureMonths) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0 || tenureMonths <= 0) {
            return BigDecimal.ZERO;
        }

        double monthlyRate = (annualInterestRate / 100.0) / 12.0;
        if (monthlyRate == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        double p = principal.doubleValue();
        double emiVal = (p * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return BigDecimal.valueOf(emiVal).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingLoans() {
        return loanRepository.countByStatus(LoanStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalApprovedLoans() {
        BigDecimal total = loanRepository.sumApprovedLoanAmount();
        return total != null ? total : BigDecimal.ZERO;
    }
}
