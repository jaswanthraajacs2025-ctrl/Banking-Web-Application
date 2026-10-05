package com.bankingsystem.controller;

import com.bankingsystem.dto.DepositRequest;
import com.bankingsystem.dto.WithdrawRequest;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.AccountService;
import com.bankingsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerAccountController {

    private final UserService userService;
    private final AccountService accountService;

    public CustomerAccountController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public String viewAccount(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("account", user.getAccount());
        model.addAttribute("pageTitle", "My Account Details - Apex Horizon Bank");
        return "customer/account";
    }

    @GetMapping("/deposit")
    public String depositPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (!model.containsAttribute("depositRequest")) {
            model.addAttribute("depositRequest", new DepositRequest());
        }
        model.addAttribute("account", user.getAccount());
        model.addAttribute("pageTitle", "Deposit Funds - Apex Horizon Bank");
        return "customer/deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(@Valid @ModelAttribute("depositRequest") DepositRequest request,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest servletRequest,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "Deposit Funds - Apex Horizon Bank");
            return "customer/deposit";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            Transaction tx = accountService.depositMoney(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "$" + request.getAmount() + " successfully deposited! Reference: " + tx.getReferenceNumber());
            return "redirect:/customer/transactions";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "Deposit Funds - Apex Horizon Bank");
            return "customer/deposit";
        }
    }

    @GetMapping("/withdraw")
    public String withdrawPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (!model.containsAttribute("withdrawRequest")) {
            model.addAttribute("withdrawRequest", new WithdrawRequest());
        }
        model.addAttribute("account", user.getAccount());
        model.addAttribute("pageTitle", "Withdraw Funds - Apex Horizon Bank");
        return "customer/withdraw";
    }

    @PostMapping("/withdraw")
    public String processWithdrawal(@Valid @ModelAttribute("withdrawRequest") WithdrawRequest request,
                                    BindingResult result,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    HttpServletRequest servletRequest,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "Withdraw Funds - Apex Horizon Bank");
            return "customer/withdraw";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            Transaction tx = accountService.withdrawMoney(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "$" + request.getAmount() + " successfully withdrawn. Reference: " + tx.getReferenceNumber());
            return "redirect:/customer/transactions";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "Withdraw Funds - Apex Horizon Bank");
            return "customer/withdraw";
        }
    }
}
