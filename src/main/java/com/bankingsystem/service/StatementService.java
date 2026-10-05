package com.bankingsystem.service;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.User;

import java.time.LocalDate;

public interface StatementService {
    byte[] generatePdfStatement(User user, Account account, LocalDate startDate, LocalDate endDate);
    byte[] generateCsvStatement(Account account, LocalDate startDate, LocalDate endDate);
}
