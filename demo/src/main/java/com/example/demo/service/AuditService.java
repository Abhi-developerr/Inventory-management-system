package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.AuditLog;
import com.example.demo.entity.User;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Audit Service
 * Handles audit logging for all critical operations
 */
@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired(required = false)
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Log an audit entry
     */
    @Transactional
    public void log(AuditLog.ActionType action, AuditLog.EntityType entityType, 
                   Long entityId, Object oldValue, Object newValue, String description) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
            String username = SecurityUtils.getCurrentUsername();
            
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .organizationId(organizationId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null)
                    .newValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null)
                    .ipAddress(getClientIpAddress())
                    .userAgent(getUserAgent())
                    .description(description)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    /**
     * Get audit logs for organization
     */
    public Page<AuditLog> getAuditLogs(Long organizationId, Pageable pageable) {
        return auditLogRepository.findByOrganizationIdOrderByTimestampDesc(organizationId, pageable);
    }

    /**
     * Get audit logs for specific entity
     */
    public Page<AuditLog> getEntityAuditLogs(AuditLog.EntityType entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId, pageable);
    }

    private String getClientIpAddress() {
        if (request == null) return "Unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent() {
        if (request == null) return "Unknown";
        return request.getHeader("User-Agent");
    }
}
