package com.bankingsystem.controller;

import com.bankingsystem.dto.CardLimitRequest;
import com.bankingsystem.dto.CardRequest;
import com.bankingsystem.entity.Card;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.service.CardService;
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
@RequestMapping("/customer/cards")
public class CustomerCardController {

    private final UserService userService;
    private final CardService cardService;

    public CustomerCardController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping
    public String viewCards(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        List<Card> cards = cardService.getCardsForUser(user);
        if (!model.containsAttribute("cardRequest")) {
            model.addAttribute("cardRequest", new CardRequest());
        }
        if (!model.containsAttribute("cardLimitRequest")) {
            model.addAttribute("cardLimitRequest", new CardLimitRequest());
        }
        model.addAttribute("cards", cards);
        model.addAttribute("account", user.getAccount());
        model.addAttribute("pageTitle", "Cards Management - Apex Horizon Bank");
        return "customer/cards";
    }

    @PostMapping("/request")
    public String requestCard(@Valid @ModelAttribute("cardRequest") CardRequest request,
                              BindingResult result,
                              @AuthenticationPrincipal UserDetails userDetails,
                              HttpServletRequest servletRequest,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("cards", cardService.getCardsForUser(user));
            model.addAttribute("account", user.getAccount());
            model.addAttribute("pageTitle", "Cards Management - Apex Horizon Bank");
            return "customer/cards";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            Card card = cardService.requestCard(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage",
                    "New " + card.getCardType() + " card (" + card.getCardNumberMasked() + ") requested and activated successfully!");
            return "redirect:/customer/cards";
        } catch (BankingException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/cards";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable("id") Long cardId,
                               @RequestParam("status") CardStatus newStatus,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest servletRequest,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        try {
            String ip = servletRequest.getRemoteAddr();
            cardService.toggleCardStatus(user, cardId, newStatus, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Card status updated to " + newStatus);
        } catch (BankingException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/cards";
    }

    @PostMapping("/limits")
    public String updateLimits(@Valid @ModelAttribute("cardLimitRequest") CardLimitRequest request,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest servletRequest,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid spending limit values. Please try again.");
            return "redirect:/customer/cards";
        }

        try {
            String ip = servletRequest.getRemoteAddr();
            cardService.updateCardLimits(user, request, ip);
            redirectAttributes.addFlashAttribute("successMessage", "Card limits updated successfully.");
        } catch (BankingException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/cards";
    }
}
