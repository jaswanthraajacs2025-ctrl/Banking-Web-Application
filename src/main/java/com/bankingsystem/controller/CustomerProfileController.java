package com.bankingsystem.controller;

import com.bankingsystem.dto.PasswordChangeRequest;
import com.bankingsystem.dto.ProfileUpdateRequest;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
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
public class CustomerProfileController {

    private final UserService userService;

    public CustomerProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        ProfileUpdateRequest profileRequest = new ProfileUpdateRequest();
        profileRequest.setFullName(user.getFullName());
        profileRequest.setMobileNumber(user.getMobileNumber());
        profileRequest.setDateOfBirth(user.getDateOfBirth());
        profileRequest.setAddress(user.getAddress());

        model.addAttribute("user", user);
        model.addAttribute("account", user.getAccount());
        model.addAttribute("profileRequest", profileRequest);
        model.addAttribute("pageTitle", "My Profile - Apex Horizon Bank");
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileRequest") ProfileUpdateRequest request,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletRequest servletRequest,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "My Profile - Apex Horizon Bank");
            return "customer/profile";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            userService.updateProfile(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Profile information successfully updated.");
            return "redirect:/customer/profile";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "My Profile - Apex Horizon Bank");
            return "customer/profile";
        }
    }

    @GetMapping("/settings")
    public String viewSettings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (!model.containsAttribute("passwordRequest")) {
            model.addAttribute("passwordRequest", new PasswordChangeRequest());
        }
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Security & Settings - Apex Horizon Bank");
        return "customer/settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordRequest") PasswordChangeRequest request,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest servletRequest,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Security & Settings - Apex Horizon Bank");
            return "customer/settings";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            userService.changePassword(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
            return "redirect:/customer/settings";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Security & Settings - Apex Horizon Bank");
            return "customer/settings";
        }
    }
}
