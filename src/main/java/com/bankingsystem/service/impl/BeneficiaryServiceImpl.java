package com.bankingsystem.service.impl;

import com.bankingsystem.dto.BeneficiaryRequest;
import com.bankingsystem.entity.AuditAction;
import com.bankingsystem.entity.Beneficiary;
import com.bankingsystem.entity.User;
import com.bankingsystem.exception.BankingException;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.BeneficiaryRepository;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.BeneficiaryService;
import com.bankingsystem.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public BeneficiaryServiceImpl(BeneficiaryRepository beneficiaryRepository,
                                  NotificationService notificationService,
                                  AuditLogService auditLogService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Beneficiary addBeneficiary(User user, BeneficiaryRequest request, String ipAddress) {
        if (!request.getAccountNumber().trim().equals(request.getConfirmAccountNumber().trim())) {
            throw new BankingException("Account numbers do not match.");
        }

        String accNo = request.getAccountNumber().trim();
        if (user.getAccount() != null && user.getAccount().getAccountNumber().equalsIgnoreCase(accNo)) {
            throw new BankingException("You cannot add your own account as a beneficiary.");
        }

        if (beneficiaryRepository.existsByUserAndAccountNumber(user, accNo)) {
            throw new BankingException("A beneficiary with this account number is already saved.");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setName(request.getName().trim());
        beneficiary.setAccountNumber(accNo);
        beneficiary.setBankName(request.getBankName().trim());
        beneficiary.setIfsc(request.getIfsc() != null ? request.getIfsc().trim() : "APEX0001099");
        beneficiary.setNickname(request.getNickname() != null ? request.getNickname().trim() : "");

        Beneficiary saved = beneficiaryRepository.save(beneficiary);

        auditLogService.log(user.getEmail(), AuditAction.BENEFICIARY_ADDED,
                "Added beneficiary: " + saved.getName() + " (" + saved.getAccountNumber() + ")", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Beneficiary Added",
                "Beneficiary " + saved.getName() + " was successfully added to your account.", "SUCCESS", "/customer/beneficiaries");

        return saved;
    }

    @Override
    @Transactional
    public Beneficiary updateBeneficiary(User user, Long id, BeneficiaryRequest request, String ipAddress) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found."));

        beneficiary.setName(request.getName().trim());
        beneficiary.setBankName(request.getBankName().trim());
        if (request.getIfsc() != null) {
            beneficiary.setIfsc(request.getIfsc().trim());
        }
        if (request.getNickname() != null) {
            beneficiary.setNickname(request.getNickname().trim());
        }

        Beneficiary updated = beneficiaryRepository.save(beneficiary);
        auditLogService.log(user.getEmail(), AuditAction.BENEFICIARY_ADDED,
                "Updated beneficiary: " + updated.getName() + " (" + updated.getAccountNumber() + ")", ipAddress, "SUCCESS");

        return updated;
    }

    @Override
    @Transactional
    public void deleteBeneficiary(User user, Long id, String ipAddress) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found."));

        beneficiaryRepository.delete(beneficiary);

        auditLogService.log(user.getEmail(), AuditAction.BENEFICIARY_DELETED,
                "Deleted beneficiary: " + beneficiary.getName() + " (" + beneficiary.getAccountNumber() + ")", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Beneficiary Removed",
                "Beneficiary " + beneficiary.getName() + " has been removed.", "INFO", "/customer/beneficiaries");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficiary> getBeneficiariesForUser(User user) {
        return beneficiaryRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Beneficiary> getBeneficiaryByIdAndUser(Long id, User user) {
        return beneficiaryRepository.findByIdAndUser(id, user);
    }
}
