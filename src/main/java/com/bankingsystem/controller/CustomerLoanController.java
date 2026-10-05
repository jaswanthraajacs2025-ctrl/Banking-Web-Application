package com.bankingsystem.controller;

import com.bankingsystem.dto.LoanApplicationRequest;
import com.bankingsystem.entity.Loan;
import com.bankingsystem.entity.LoanType;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.LoanService;
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

import java.util.List;

@Controller
@RequestMapping("/customer/loans")
public class CustomerLoanController {

    private final UserService userService;
    private final LoanService loanService;

    public CustomerLoanController(UserService userService, LoanService loanService) {
        this.userService = userService;
        this.loanService = loanService;
    }

    @GetMapping
    public String viewLoans(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        List<Loan> loans = loanService.getLoansForUser(user);
        if (!model.containsAttribute("loanRequest")) {
            model.addAttribute("loanRequest", new LoanApplicationRequest());
        }
        model.addAttribute("loans", loans);
        model.addAttribute("loanTypes", LoanType.values());
        model.addAttribute("pageTitle", "Loans & Financing - Apex Horizon Bank");
        return "customer/loans";
    }

    @PostMapping("/apply")
    public String applyForLoan(@Valid @ModelAttribute("loanRequest") LoanApplicationRequest request,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest servletRequest,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("loans", loanService.getLoansForUser(user));
            model.addAttribute("loanTypes", LoanType.values());
            model.addAttribute("pageTitle", "Loans & Financing - Apex Horizon Bank");
            return "customer/loans";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            Loan loan = loanService.applyForLoan(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Loan application for $" + loan.getRequestedAmount() + " submitted successfully! Application ID: #" + loan.getId());
            return "redirect:/customer/loans";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("loans", loanService.getLoansForUser(user));
            model.addAttribute("loanTypes", LoanType.values());
            model.addAttribute("pageTitle", "Loans & Financing - Apex Horizon Bank");
            return "customer/loans";
        }
    }
}
