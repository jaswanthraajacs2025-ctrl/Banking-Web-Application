package com.bankingsystem.controller;

import com.bankingsystem.dto.TransactionFilterDTO;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import com.bankingsystem.service.StatementService;
import com.bankingsystem.service.TransactionService;
import com.bankingsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/customer/transactions")
public class CustomerTransactionController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final StatementService statementService;

    public CustomerTransactionController(UserService userService,
                                         TransactionService transactionService,
                                         StatementService statementService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.statementService = statementService;
    }

    @GetMapping
    public String viewTransactions(@ModelAttribute("filter") TransactionFilterDTO filter,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        Account account = user.getAccount();
        if (account != null) {
            Page<Transaction> transactionPage = transactionService.getAccountTransactions(account, filter);
            model.addAttribute("transactionPage", transactionPage);
            model.addAttribute("transactions", transactionPage.getContent());
            model.addAttribute("account", account);
        }

        model.addAttribute("pageTitle", "Transaction History - Apex Horizon Bank");
        return "customer/transactions";
    }

    @GetMapping("/statement/pdf")
    public ResponseEntity<byte[]> downloadPdfStatement(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getAccount() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = statementService.generatePdfStatement(user, user.getAccount(), startDate, endDate);

        String filename = "ApexHorizon_Statement_" + user.getAccount().getAccountNumber() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/statement/csv")
    public ResponseEntity<byte[]> downloadCsvStatement(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getAccount() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] csvBytes = statementService.generateCsvStatement(user.getAccount(), startDate, endDate);

        String filename = "ApexHorizon_Statement_" + user.getAccount().getAccountNumber() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
