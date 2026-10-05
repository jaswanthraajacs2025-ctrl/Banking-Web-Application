package com.bankingsystem.service;

import com.bankingsystem.dto.LoanApplicationRequest;
import com.bankingsystem.dto.LoanApprovalRequest;
import com.bankingsystem.entity.Loan;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan applyForLoan(User user, LoanApplicationRequest request, String ipAddress);
    List<Loan> getLoansForUser(User user);
    Optional<Loan> getLoanByIdAndUser(Long id, User user);
    Page<Loan> getAllLoansAdmin(LoanStatus status, String search, Pageable pageable);
    Loan reviewLoan(LoanApprovalRequest request, String adminEmail, String ipAddress);
    BigDecimal calculateEmi(BigDecimal principal, double annualInterestRate, int tenureMonths);
    long countPendingLoans();
    BigDecimal getTotalApprovedLoans();
}
