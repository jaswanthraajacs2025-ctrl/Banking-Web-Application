package com.bankingsystem.service.impl;

import com.bankingsystem.dto.TransactionFilterDTO;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.TransactionType;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findByReferenceNumber(String referenceNumber) {
        if (referenceNumber == null) return Optional.empty();
        return transactionRepository.findByReferenceNumber(referenceNumber.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(Account account) {
        return transactionRepository.findTop10ByAccountOrderByCreatedAtDesc(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAccountTransactions(Account account, TransactionFilterDTO filter) {
        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime end = filter.getEndDate() != null ? filter.getEndDate().atTime(LocalTime.MAX) : null;
        String query = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) ? filter.getSearch().trim() : null;

        Pageable pageable = PageRequest.of(Math.max(0, filter.getPage()), Math.max(1, filter.getSize()), Sort.by("createdAt").descending());
        return transactionRepository.filterAccountTransactions(account, filter.getType(), filter.getStatus(), start, end, query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAllTransactionsAdmin(TransactionFilterDTO filter, Pageable pageable) {
        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime end = filter.getEndDate() != null ? filter.getEndDate().atTime(LocalTime.MAX) : null;
        String query = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) ? filter.getSearch().trim() : null;

        return transactionRepository.filterAllTransactionsAdmin(filter.getType(), filter.getStatus(), start, end, query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountIncome(Account account) {
        BigDecimal income = transactionRepository.sumIncomeForAccount(account, account.getAccountNumber());
        return income != null ? income : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountExpenses(Account account) {
        BigDecimal expenses = transactionRepository.sumExpensesForAccount(account, account.getAccountNumber());
        return expenses != null ? expenses : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDepositsVolume() {
        BigDecimal total = transactionRepository.sumAmountByType(TransactionType.DEPOSIT);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalWithdrawalsVolume() {
        BigDecimal total = transactionRepository.sumAmountByType(TransactionType.WITHDRAWAL);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalTransfersVolume() {
        BigDecimal total = transactionRepository.sumAmountByType(TransactionType.TRANSFER);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalFeesCollected() {
        BigDecimal total = transactionRepository.sumTotalFeesCollected();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long countTransactions() {
        return transactionRepository.count();
    }
}
