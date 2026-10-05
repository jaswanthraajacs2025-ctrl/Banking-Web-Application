package com.bankingsystem.service;

import com.bankingsystem.dto.PasswordChangeRequest;
import com.bankingsystem.dto.ProfileUpdateRequest;
import com.bankingsystem.dto.RegisterRequest;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User registerCustomer(RegisterRequest request, String ipAddress);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByCustomerId(String customerId);
    User updateProfile(User user, ProfileUpdateRequest request, String ipAddress);
    void changePassword(User user, PasswordChangeRequest request, String ipAddress);
    Page<User> getCustomers(String search, Pageable pageable);
    void toggleUserStatus(Long userId, boolean enabled, String adminEmail, String ipAddress);
    long countCustomers();
}
