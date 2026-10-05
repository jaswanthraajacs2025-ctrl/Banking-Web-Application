package com.bankingsystem.controller;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.AccountStatus;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    private final UserService userService;
    private final AccountService accountService;
    private final CardService cardService;
    private final LoanService loanService;
    private final TransactionService transactionService;

    public AdminCustomerController(UserService userService,
                                   AccountService accountService,
                                   CardService cardService,
                                   LoanService loanService,
                                   TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.cardService = cardService;
        this.loanService = loanService;
        this.transactionService = transactionService;
    }

    @GetMapping("/customers")
    public String listCustomers(@RequestParam(name = "search", required = false) String search,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                Model model) {
        Page<User> customersPage = userService.getCustomers(search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("customersPage", customersPage);
        model.addAttribute("customers", customersPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Customer Accounts Management - Apex Horizon Admin");
        return "admin/customers";
    }

    @GetMapping("/customers/{id}")
    public String customerDetails(@PathVariable("id") Long id, Model model) {
        User customer = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        model.addAttribute("customer", customer);
        model.addAttribute("account", customer.getAccount());
        model.addAttribute("cards", cardService.getCardsForUser(customer));
        model.addAttribute("loans", loanService.getLoansForUser(customer));
        if (customer.getAccount() != null) {
            model.addAttribute("recentTransactions", transactionService.getRecentTransactions(customer.getAccount()));
        }
        model.addAttribute("pageTitle", "Customer Details: " + customer.getFullName() + " - Apex Horizon Admin");
        return "admin/customer-details";
    }

    @PostMapping("/customers/{id}/toggle-status")
    public String toggleCustomerStatus(@PathVariable("id") Long id,
                                       @RequestParam("enabled") boolean enabled,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        try {
            String ip = request.getRemoteAddr();
            userService.toggleUserStatus(id, enabled, userDetails.getUsername(), ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Customer status has been updated to: " + (enabled ? "ACTIVE" : "SUSPENDED"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/customers/" + id;
    }

    @GetMapping("/accounts")
    public String listAccounts(@RequestParam(name = "search", required = false) String search,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               Model model) {
        Page<Account> accountsPage = accountService.searchAccounts(search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("accountsPage", accountsPage);
        model.addAttribute("accounts", accountsPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Bank Accounts Ledger - Apex Horizon Admin");
        return "admin/accounts";
    }

    @PostMapping("/accounts/{id}/status")
    public String updateAccountStatus(@PathVariable("id") Long id,
                                      @RequestParam("status") AccountStatus status,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        try {
            String ip = request.getRemoteAddr();
            accountService.updateAccountStatus(id, status, userDetails.getUsername(), ip);
            redirectAttributes.addFlashAttribute("successMessage", "Account status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/accounts";
    }
}
