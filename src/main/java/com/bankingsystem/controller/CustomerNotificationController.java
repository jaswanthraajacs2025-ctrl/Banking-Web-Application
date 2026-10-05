package com.bankingsystem.controller;

import com.bankingsystem.entity.Notification;
import com.bankingsystem.entity.User;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/notifications")
public class CustomerNotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    public CustomerNotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String viewNotifications(@RequestParam(name = "page", defaultValue = "0") int page,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        Pageable pageable = PageRequest.of(page, 15);
        Page<Notification> notificationPage = notificationService.getNotifications(user, pageable);

        model.addAttribute("notificationPage", notificationPage);
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("pageTitle", "Notifications - Apex Horizon Bank");
        return "customer/notifications";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user != null) {
            notificationService.markAsRead(id, user);
        }
        return "redirect:/customer/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user != null) {
            notificationService.markAllAsRead(user);
            redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        }
        return "redirect:/customer/notifications";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotification(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user != null) {
            notificationService.deleteNotification(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Notification removed.");
        }
        return "redirect:/customer/notifications";
    }
}
