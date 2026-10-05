package com.bankingsystem.controller;

import com.bankingsystem.dto.AdminDashboardStatsDTO;
import com.bankingsystem.dto.TransactionFilterDTO;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.service.AdminService;
import com.bankingsystem.service.LoanService;
import com.bankingsystem.service.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminService adminService;
    private final TransactionService transactionService;
    private final LoanService loanService;

    public AdminDashboardController(AdminService adminService,
                                    TransactionService transactionService,
                                    LoanService loanService) {
        this.adminService = adminService;
        this.transactionService = transactionService;
        this.loanService = loanService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AdminDashboardStatsDTO stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Recent 8 transactions for system monitoring
        TransactionFilterDTO filter = new TransactionFilterDTO();
        filter.setSize(8);
        model.addAttribute("recentTransactions",
                transactionService.getAllTransactionsAdmin(filter, PageRequest.of(0, 8, Sort.by("createdAt").descending())).getContent());

        // Pending loans requiring review
        model.addAttribute("pendingLoans",
                loanService.getAllLoansAdmin(LoanStatus.PENDING, null, PageRequest.of(0, 5, Sort.by("appliedAt").descending())).getContent());

        model.addAttribute("pageTitle", "Executive Admin Dashboard - Apex Horizon Bank");
        return "admin/dashboard";
    }
}
