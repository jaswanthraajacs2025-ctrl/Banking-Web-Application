package com.bankingsystem.service;

import com.bankingsystem.entity.Notification;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String title, String message, String type, String link);
    void createNotificationForEmail(String email, String title, String message, String type, String link);
    List<Notification> getRecentNotifications(User user);
    Page<Notification> getNotifications(User user, Pageable pageable);
    long getUnreadCount(User user);
    void markAsRead(Long notificationId, User user);
    void markAllAsRead(User user);
    void deleteNotification(Long notificationId, User user);
}
