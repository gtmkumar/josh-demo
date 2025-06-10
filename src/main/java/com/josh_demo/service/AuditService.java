package com.josh_demo.service;

import com.josh_demo.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
    void logAuthenticationEvent(User user, String activityType, String status, HttpServletRequest request);
    void logExceptionEvent(User user, String activityType, String description, String status, HttpServletRequest request);
} 