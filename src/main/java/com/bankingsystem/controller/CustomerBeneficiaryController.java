package com.bankingsystem.controller;

import com.bankingsystem.dto.BeneficiaryRequest;
import com.bankingsystem.entity.Beneficiary;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.BeneficiaryService;
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
@RequestMapping("/customer/beneficiaries")
public class CustomerBeneficiaryController {

    private final UserService userService;
    private final BeneficiaryService beneficiaryService;

    public CustomerBeneficiaryController(UserService userService, BeneficiaryService beneficiaryService) {
        this.userService = userService;
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    public String viewBeneficiaries(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiariesForUser(user);
        if (!model.containsAttribute("beneficiaryRequest")) {
            model.addAttribute("beneficiaryRequest", new BeneficiaryRequest());
        }
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("pageTitle", "Manage Beneficiaries - Apex Horizon Bank");
        return "customer/beneficiaries";
    }

    @PostMapping("/add")
    public String addBeneficiary(@Valid @ModelAttribute("beneficiaryRequest") BeneficiaryRequest request,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest servletRequest,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesForUser(user));
            model.addAttribute("pageTitle", "Manage Beneficiaries - Apex Horizon Bank");
            return "customer/beneficiaries";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            beneficiaryService.addBeneficiary(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Beneficiary added successfully!");
            return "redirect:/customer/beneficiaries";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesForUser(user));
            model.addAttribute("pageTitle", "Manage Beneficiaries - Apex Horizon Bank");
            return "customer/beneficiaries";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBeneficiary(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    HttpServletRequest servletRequest,
                                    RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        try {
            String ip = servletRequest.getRemoteAddr();
            beneficiaryService.deleteBeneficiary(user, id, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Beneficiary removed successfully.");
        } catch (BankingException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/beneficiaries";
    }
}
