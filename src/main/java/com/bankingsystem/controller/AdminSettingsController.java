package com.bankingsystem.controller;

import com.bankingsystem.entity.AuditAction;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.SystemSettingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminSettingsController {

    private final SystemSettingService systemSettingService;
    private final AuditLogService auditLogService;

    public AdminSettingsController(SystemSettingService systemSettingService, AuditLogService auditLogService) {
        this.systemSettingService = systemSettingService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/settings")
    public String viewSettings(Model model) {
        model.addAttribute("settings", systemSettingService.getAllSettings());
        model.addAttribute("transferFeePercent", systemSettingService.getSettingValue("TRANSFER_FEE_PERCENT", "0.25"));
        model.addAttribute("serviceTaxPercent", systemSettingService.getSettingValue("SERVICE_TAX_PERCENT", "0.10"));
        model.addAttribute("savingsInterestRate", systemSettingService.getSettingValue("SAVINGS_INTEREST_RATE", "4.5"));
        model.addAttribute("dailyTransferLimit", systemSettingService.getSettingValue("DAILY_TRANSFER_LIMIT", "50000.00"));
        model.addAttribute("minAccountBalance", systemSettingService.getSettingValue("MIN_ACCOUNT_BALANCE", "50.00"));
        model.addAttribute("supportHotline", systemSettingService.getSettingValue("SUPPORT_HOTLINE", "+1 (800) 555-APEX"));
        model.addAttribute("pageTitle", "System & Fee Configuration - Apex Horizon Admin");
        return "admin/settings";
    }

    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> allParams,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            String ip = request.getRemoteAddr();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (!entry.getKey().startsWith("_") && !entry.getKey().equals("submit")) {
                    systemSettingService.updateSetting(entry.getKey(), entry.getValue().trim());
                }
            }
            auditLogService.log(userDetails.getUsername(), AuditAction.SETTINGS_UPDATED, "Administrator updated core system fee & limit settings", ip, "SUCCESS");
            redirectAttributes.addFlashAttribute("successMessage", "System banking settings updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating settings: " + e.getMessage());
        }
        return "redirect:/admin/settings";
    }
}
