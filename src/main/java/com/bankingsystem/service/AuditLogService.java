package com.bankingsystem.service;

import com.bankingsystem.entity.AuditAction;
import com.bankingsystem.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {
    void log(String userEmail, AuditAction action, String description, String ipAddress, String status);
    Page<AuditLog> getAuditLogs(AuditAction action, LocalDateTime startDate, LocalDateTime endDate, String search, Pageable pageable);
}
