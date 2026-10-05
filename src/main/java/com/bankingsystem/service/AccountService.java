package com.bankingsystem.service;

import com.bankingsystem.dto.DepositRequest;
import com.bankingsystem.dto.WithdrawRequest;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.AccountStatus;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUser(User user);
    Account getAccountByAccountNumberOrThrow(String accountNumber);
    Transaction depositMoney(User user, DepositRequest request, String ipAddress);
    Transaction withdrawMoney(User user, WithdrawRequest request, String ipAddress);
    void updateAccountStatus(Long accountId, AccountStatus status, String adminEmail, String ipAddress);
    Page<Account> searchAccounts(String search, Pageable pageable);
    long countByStatus(AccountStatus status);
    BigDecimal getTotalActiveBalance();
}
