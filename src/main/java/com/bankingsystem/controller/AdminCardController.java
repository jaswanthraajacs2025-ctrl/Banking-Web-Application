package com.bankingsystem.controller;

import com.bankingsystem.entity.Card;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/cards")
    public String listCards(@RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            Model model) {
        Page<Card> cardsPage = cardService.getAllCardsAdmin(PageRequest.of(page, size));
        model.addAttribute("cardsPage", cardsPage);
        model.addAttribute("cards", cardsPage.getContent());
        model.addAttribute("pageTitle", "Issued Cards Management - Apex Horizon Admin");
        return "admin/cards";
    }

    @PostMapping("/cards/{id}/status")
    public String updateCardStatus(@PathVariable("id") Long id,
                                   @RequestParam("status") CardStatus status,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            String ip = request.getRemoteAddr();
            cardService.adminUpdateCardStatus(id, status, userDetails.getUsername(), ip);
            redirectAttributes.addFlashAttribute("successMessage", "Card status changed to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/cards";
    }
}
