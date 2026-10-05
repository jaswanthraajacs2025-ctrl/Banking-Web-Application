package com.bankingsystem.controller;

import com.bankingsystem.entity.AccountStatus;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final CardService cardService;

    public AdminReportController(UserService userService,
                                 AccountService accountService,
                                 TransactionService transactionService,
                                 LoanService loanService,
                                 CardService cardService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.loanService = loanService;
        this.cardService = cardService;
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("totalCustomers", userService.countCustomers());
        model.addAttribute("activeAccounts", accountService.countByStatus(AccountStatus.ACTIVE));
        model.addAttribute("frozenAccounts", accountService.countByStatus(AccountStatus.FROZEN));
        model.addAttribute("totalSystemBalance", accountService.getTotalActiveBalance());
        model.addAttribute("totalDepositsVolume", transactionService.getTotalDepositsVolume());
        model.addAttribute("totalWithdrawalsVolume", transactionService.getTotalWithdrawalsVolume());
        model.addAttribute("totalTransfersVolume", transactionService.getTotalTransfersVolume());
        model.addAttribute("totalFeesCollected", transactionService.getTotalFeesCollected());
        model.addAttribute("pendingLoansCount", loanService.countPendingLoans());
        model.addAttribute("totalApprovedLoans", loanService.getTotalApprovedLoans());
        model.addAttribute("activeCardsCount", cardService.countActiveCards());
        model.addAttribute("totalTransactionsCount", transactionService.countTransactions());

        model.addAttribute("pageTitle", "Financial Intelligence & Reports - Apex Horizon Admin");
        return "admin/reports";
    }
}
