package com.bankingsystem.controller;

import com.bankingsystem.dto.ContactRequest;
import com.bankingsystem.dto.FeeCalculationDTO;
import com.bankingsystem.service.SystemSettingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class HomeController {

    private final SystemSettingService systemSettingService;

    public HomeController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Apex Horizon Bank - Banking Made Simple, Secure & Smarter");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Us - Apex Horizon Bank");
        return "about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("pageTitle", "Digital Banking Services - Apex Horizon Bank");
        return "services";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Customer Support - Apex Horizon Bank");
        if (!model.containsAttribute("contactRequest")) {
            model.addAttribute("contactRequest", new ContactRequest());
        }
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContact(@Valid @ModelAttribute("contactRequest") ContactRequest request,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contact";
        }
        redirectAttributes.addFlashAttribute("successMessage", 
                "Thank you, " + request.getName() + "! Your message has been received. Our support team will reach out within 24 hours.");
        return "redirect:/contact";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "403 - Access Denied");
        return "errors/403";
    }

    @GetMapping("/api/calculate-fee")
    @ResponseBody
    public ResponseEntity<FeeCalculationDTO> calculateFee(@RequestParam(name = "amount", defaultValue = "0") BigDecimal amount) {
        FeeCalculationDTO calculation = systemSettingService.calculateFeeAndTax(amount);
        return ResponseEntity.ok(calculation);
    }
}
