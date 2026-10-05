package com.bankingsystem.service.impl;

import com.bankingsystem.entity.Notification;
import com.bankingsystem.entity.User;
import com.bankingsystem.repository.NotificationRepository;
import com.bankingsystem.repository.UserRepository;
import com.bankingsystem.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createNotification(User user, String title, String message, String type, String link) {
        if (user != null) {
            Notification notif = new Notification(user, title, message, type != null ? type : "INFO", link);
            notificationRepository.save(notif);
        }
    }

    @Override
    @Transactional
    public void createNotificationForEmail(String email, String title, String message, String type, String link) {
        userRepository.findByEmail(email).ifPresent(user -> {
            createNotification(user, title, message, type, link);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(User user) {
        return notificationRepository.findTop5ByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(user.getId())) {
                notificationRepository.delete(notification);
            }
        });
    }
}
