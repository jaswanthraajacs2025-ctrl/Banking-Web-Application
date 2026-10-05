package com.bankingsystem.service;

import com.bankingsystem.dto.TransactionFilterDTO;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    List<Transaction> getRecentTransactions(Account account);
    Page<Transaction> getAccountTransactions(Account account, TransactionFilterDTO filter);
    Page<Transaction> getAllTransactionsAdmin(TransactionFilterDTO filter, Pageable pageable);
    BigDecimal getAccountIncome(Account account);
    BigDecimal getAccountExpenses(Account account);
    BigDecimal getTotalDepositsVolume();
    BigDecimal getTotalWithdrawalsVolume();
    BigDecimal getTotalTransfersVolume();
    BigDecimal getTotalFeesCollected();
    long countTransactions();
}
