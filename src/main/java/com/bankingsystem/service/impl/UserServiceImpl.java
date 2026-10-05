package com.bankingsystem.service.impl;

import com.bankingsystem.dto.PasswordChangeRequest;
import com.bankingsystem.dto.ProfileUpdateRequest;
import com.bankingsystem.dto.RegisterRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.UserRepository;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.service.UserService;
import com.bankingsystem.util.GeneratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           AuditLogService auditLogService,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public User registerCustomer(RegisterRequest request, String ipAddress) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BankingException("Passwords do not match. Please verify and try again.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BankingException("An account with this email address already exists.");
        }

        // 1. Create User
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress().trim());
        user.setRole(Role.ROLE_CUSTOMER);
        user.setEnabled(true);

        // Generate unique Customer ID
        String customerId;
        do {
            customerId = GeneratorUtils.generateCustomerId();
        } while (userRepository.existsByCustomerId(customerId));
        user.setCustomerId(customerId);

        // 2. Create Default Bank Account
        Account account = new Account();
        String accountNumber;
        do {
            accountNumber = GeneratorUtils.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        account.setAccountNumber(accountNumber);
        AccountType accType = "CURRENT".equalsIgnoreCase(request.getAccountType()) ? AccountType.CURRENT : AccountType.SAVINGS;
        account.setAccountType(accType);
        // Welcome bonus for demo exploration
        account.setBalance(new BigDecimal("1000.00"));
        account.setAvailableBalance(new BigDecimal("1000.00"));
        account.setCurrency("USD");
        account.setStatus(AccountStatus.ACTIVE);
        account.setDailyTransferLimit(new BigDecimal("25000.00"));
        account.setBranchName("Apex Horizon Digital Branch");
        account.setIfscCode("APEX0009988");

        user.setAccount(account);
        User savedUser = userRepository.save(user);

        // Log audit & notify
        auditLogService.log(savedUser.getEmail(), AuditAction.REGISTER, "New customer account registered. Account Number: " + accountNumber, ipAddress, "SUCCESS");
        notificationService.createNotification(savedUser, "Welcome to Apex Horizon Bank", 
                "Your account " + accountNumber + " has been activated with a $1,000.00 initial balance. Welcome aboard!", "SUCCESS", "/customer/dashboard");

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByCustomerId(String customerId) {
        return userRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public User updateProfile(User user, ProfileUpdateRequest request, String ipAddress) {
        user.setFullName(request.getFullName().trim());
        user.setMobileNumber(request.getMobileNumber().trim());
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        user.setAddress(request.getAddress().trim());
        User updated = userRepository.save(user);

        auditLogService.log(user.getEmail(), AuditAction.PROFILE_UPDATED, "Customer profile information updated", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Profile Updated", "Your profile details have been successfully updated.", "INFO", "/customer/profile");

        return updated;
    }

    @Override
    @Transactional
    public void changePassword(User user, PasswordChangeRequest request, String ipAddress) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BankingException("Current password is incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BankingException("New password confirmation does not match.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BankingException("New password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(user.getEmail(), AuditAction.PASSWORD_CHANGED, "Customer changed account password", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Security Alert: Password Changed", "Your banking portal password was successfully updated.", "WARNING", "/customer/settings");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getCustomers(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return userRepository.searchCustomers(Role.ROLE_CUSTOMER, search.trim(), pageable);
        }
        return userRepository.findByRole(Role.ROLE_CUSTOMER, pageable);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled, String adminEmail, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + userId));

        user.setEnabled(enabled);
        if (user.getAccount() != null) {
            user.getAccount().setStatus(enabled ? AccountStatus.ACTIVE : AccountStatus.FROZEN);
        }
        userRepository.save(user);

        String actionDesc = (enabled ? "Activated" : "Suspended") + " customer account: " + user.getEmail();
        auditLogService.log(adminEmail, enabled ? AuditAction.ACCOUNT_UNFREEZE : AuditAction.ACCOUNT_FREEZE, actionDesc, ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Account Status Update", 
                "Your banking account status has been set to " + (enabled ? "ACTIVE" : "FROZEN") + " by administration.", enabled ? "SUCCESS" : "DANGER", "/customer/dashboard");
    }

    @Override
    @Transactional(readOnly = true)
    public long countCustomers() {
        return userRepository.countByRole(Role.ROLE_CUSTOMER);
    }
}
