package com.bankingsystem.service.impl;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.service.StatementService;
import com.bankingsystem.util.CsvStatementGenerator;
import com.bankingsystem.util.PdfStatementGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class StatementServiceImpl implements StatementService {

    private final TransactionRepository transactionRepository;

    public StatementServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePdfStatement(User user, Account account, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        List<Transaction> transactions = transactionRepository.findForStatement(account, start, end);
        return PdfStatementGenerator.generateStatement(user, account, transactions, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCsvStatement(Account account, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        List<Transaction> transactions = transactionRepository.findForStatement(account, start, end);
        return CsvStatementGenerator.generateCsvStatement(account, transactions);
    }
}
