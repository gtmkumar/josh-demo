package com.josh_demo.service.impl;

import com.josh_demo.model.AuditLog;
import com.josh_demo.model.User;
import com.josh_demo.repository.AuditLogRepository;
import com.josh_demo.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuthenticationEvent(User user, String activityType, String status, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setActivityType(activityType);
        auditLog.setDescription("Authentication attempt");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(getClientIp(request));
        auditLog.setResourceAffected("Authentication");
        auditLog.setStatus(status);
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logExceptionEvent(User user, String activityType, String description, String status, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setActivityType(activityType);
        auditLog.setDescription(description);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(getClientIp(request));
        auditLog.setResourceAffected("System");
        auditLog.setStatus(status);
        auditLogRepository.save(auditLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
} 