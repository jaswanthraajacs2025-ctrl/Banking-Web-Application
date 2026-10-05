package com.bankingsystem.controller;

import com.bankingsystem.dto.TransferRequest;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Beneficiary;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.BeneficiaryService;
import com.bankingsystem.service.SystemSettingService;
import com.bankingsystem.service.TransferService;
import com.bankingsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerTransferController {

    private final UserService userService;
    private final TransferService transferService;
    private final BeneficiaryService beneficiaryService;
    private final SystemSettingService systemSettingService;

    public CustomerTransferController(UserService userService,
                                      TransferService transferService,
                                      BeneficiaryService beneficiaryService,
                                      SystemSettingService systemSettingService) {
        this.userService = userService;
        this.transferService = transferService;
        this.beneficiaryService = beneficiaryService;
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/transfer")
    public String transferPage(@RequestParam(name = "beneficiaryId", required = false) Long beneficiaryId,
                               @RequestParam(name = "accountNumber", required = false) String accountNumber,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        TransferRequest request = new TransferRequest();
        if (beneficiaryId != null) {
            request.setBeneficiaryId(beneficiaryId);
            beneficiaryService.getBeneficiaryByIdAndUser(beneficiaryId, user)
                    .ifPresent(b -> request.setRecipientAccountNumber(b.getAccountNumber()));
        } else if (accountNumber != null) {
            request.setRecipientAccountNumber(accountNumber);
        }

        prepareTransferModel(model, user, request);
        return "customer/transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(@Valid @ModelAttribute("transferRequest") TransferRequest request,
                                  BindingResult result,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  HttpServletRequest servletRequest,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            prepareTransferModel(model, user, request);
            return "customer/transfer";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            Transaction tx = transferService.transferMoney(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transfer of $" + tx.getAmount() + " completed successfully! Reference Number: " + tx.getReferenceNumber());
            return "redirect:/customer/transactions";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepareTransferModel(model, user, request);
            return "customer/transfer";
        }
    }

    private void prepareTransferModel(Model model, User user, TransferRequest request) {
        Account account = user.getAccount();
        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiariesForUser(user);

        model.addAttribute("transferRequest", request);
        model.addAttribute("account", account);
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("transferFeePercent", systemSettingService.getDoubleSetting("TRANSFER_FEE_PERCENT", 0.25));
        model.addAttribute("serviceTaxPercent", systemSettingService.getDoubleSetting("SERVICE_TAX_PERCENT", 0.10));
        model.addAttribute("pageTitle", "Transfer Funds - Apex Horizon Bank");
    }
}
