package com.bankingsystem.controller;

import com.bankingsystem.entity.Notification;
import com.bankingsystem.entity.User;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalModelAttributesAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    public GlobalModelAttributesAdvice(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return userService.findByEmail(authentication.getName()).orElse(null);
        }
        return null;
    }

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                return notificationService.getUnreadCount(user);
            }
        }
        return 0;
    }

    @ModelAttribute("headerNotifications")
    public List<Notification> getHeaderNotifications(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                return notificationService.getRecentNotifications(user);
            }
        }
        return Collections.emptyList();
    }
}
