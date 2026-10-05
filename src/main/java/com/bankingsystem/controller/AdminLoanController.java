package com.bankingsystem.controller;

import com.bankingsystem.dto.LoanApprovalRequest;
import com.bankingsystem.entity.Loan;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
public class AdminLoanController {

    private final LoanService loanService;

    public AdminLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/loans")
    public String listLoans(@RequestParam(name = "status", required = false) LoanStatus status,
                            @RequestParam(name = "search", required = false) String search,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            Model model) {
        Page<Loan> loansPage = loanService.getAllLoansAdmin(status, search, PageRequest.of(page, size, Sort.by("appliedAt").descending()));
        model.addAttribute("loansPage", loansPage);
        model.addAttribute("loans", loansPage.getContent());
        model.addAttribute("currentStatus", status);
        model.addAttribute("search", search);
        model.addAttribute("loanStatuses", LoanStatus.values());
        model.addAttribute("pageTitle", "Loan Applications Review - Apex Horizon Admin");
        return "admin/loans";
    }

    @PostMapping("/loans/review")
    public String reviewLoan(@Valid @ModelAttribute("approvalRequest") LoanApprovalRequest request,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest servletRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            String ip = servletRequest.getRemoteAddr();
            Loan loan = loanService.reviewLoan(request, userDetails.getUsername(), ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Loan application #" + loan.getId() + " has been " + loan.getStatus() + " successfully.");
        } catch (BankingException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/loans";
    }
}
