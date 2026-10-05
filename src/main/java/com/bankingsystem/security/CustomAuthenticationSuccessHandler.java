package com.bankingsystem.security;

import com.bankingsystem.entity.AuditAction;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public CustomAuthenticationSuccessHandler(AuditLogService auditLogService, NotificationService notificationService) {
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        String ipAddress = request.getRemoteAddr();

        auditLogService.log(email, AuditAction.LOGIN, "Successful login to Apex Horizon Banking portal", ipAddress, "SUCCESS");

        notificationService.createNotificationForEmail(email, "New Login Detected", "You have successfully logged in from IP " + ipAddress, "INFO", "/customer/dashboard");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            } else if ("ROLE_CUSTOMER".equals(authority.getAuthority())) {
                response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
}
