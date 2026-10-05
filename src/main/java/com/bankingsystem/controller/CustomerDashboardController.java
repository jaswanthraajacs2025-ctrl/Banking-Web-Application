package com.bankingsystem.controller;

import com.bankingsystem.entity.*;
import com.bankingsystem.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final CardService cardService;
    private final BeneficiaryService beneficiaryService;
    private final LoanService loanService;

    public CustomerDashboardController(UserService userService,
                                       TransactionService transactionService,
                                       CardService cardService,
                                       BeneficiaryService beneficiaryService,
                                       LoanService loanService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.cardService = cardService;
        this.beneficiaryService = beneficiaryService;
        this.loanService = loanService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Account account = user.getAccount();
        model.addAttribute("user", user);
        model.addAttribute("account", account);

        if (account != null) {
            List<Transaction> recentTransactions = transactionService.getRecentTransactions(account);
            model.addAttribute("recentTransactions", recentTransactions);

            BigDecimal income = transactionService.getAccountIncome(account);
            BigDecimal expenses = transactionService.getAccountExpenses(account);
            model.addAttribute("totalIncome", income);
            model.addAttribute("totalExpenses", expenses);
        } else {
            model.addAttribute("recentTransactions", Collections.emptyList());
            model.addAttribute("totalIncome", BigDecimal.ZERO);
            model.addAttribute("totalExpenses", BigDecimal.ZERO);
        }

        List<Card> cards = cardService.getCardsForUser(user);
        model.addAttribute("cards", cards);

        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiariesForUser(user);
        model.addAttribute("beneficiaries", beneficiaries);

        List<Loan> loans = loanService.getLoansForUser(user);
        model.addAttribute("loans", loans);

        model.addAttribute("pageTitle", "Customer Dashboard - Apex Horizon Bank");
        return "customer/dashboard";
    }
}
