package com.bankingsystem.controller;

import com.bankingsystem.dto.TransactionFilterDTO;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.TransactionStatus;
import com.bankingsystem.entity.TransactionType;
import com.bankingsystem.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminTransactionController {

    private final TransactionService transactionService;

    public AdminTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public String listTransactions(@ModelAttribute("filter") TransactionFilterDTO filter,
                                   Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, filter.getPage()), Math.max(1, filter.getSize()), Sort.by("createdAt").descending());
        Page<Transaction> transactionPage = transactionService.getAllTransactionsAdmin(filter, pageable);

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("transactionStatuses", TransactionStatus.values());
        model.addAttribute("pageTitle", "Global Transactions Log - Apex Horizon Admin");
        return "admin/transactions";
    }
}
