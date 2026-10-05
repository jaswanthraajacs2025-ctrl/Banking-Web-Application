package com.bankingsystem.controller;

import com.bankingsystem.dto.RegisterRequest;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            @RequestParam(name = "registered", required = false) String registered,
                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/customer/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please verify your credentials.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been securely logged out of your session.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Account created successfully! Please sign in below.");
        }

        model.addAttribute("pageTitle", "Sign In - Apex Horizon Bank");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/customer/dashboard";
        }

        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        model.addAttribute("pageTitle", "Open an Account - Apex Horizon Bank");
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                 BindingResult result,
                                 HttpServletRequest servletRequest,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Open an Account - Apex Horizon Bank");
            return "register";
        }

        try {
            String ipAddress = servletRequest.getRemoteAddr();
            User user = userService.registerCustomer(request, ipAddress);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome to Apex Horizon Bank, " + user.getFullName() + "! Your account (" + user.getAccount().getAccountNumber() + ") is active. You can now log in.");
            return "redirect:/login";
        } catch (BankingException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("pageTitle", "Open an Account - Apex Horizon Bank");
            return "register";
        }
    }
}
