package com.bankingsystem.service.impl;

import com.bankingsystem.entity.AuditAction;
import com.bankingsystem.entity.AuditLog;
import com.bankingsystem.repository.AuditLogRepository;
import com.bankingsystem.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void log(String userEmail, AuditAction action, String description, String ipAddress, String status) {
        try {
            AuditLog auditLog = new AuditLog(userEmail, action, description, ipAddress, status != null ? status : "SUCCESS");
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Ensure audit logging failures do not break the main transaction flow
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(AuditAction action, LocalDateTime startDate, LocalDateTime endDate, String search, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return auditLogRepository.filterAuditLogs(action, startDate, endDate, query, pageable);
    }
}
