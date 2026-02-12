package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.AuditLog;
import com.example.demo.service.AuditService;
import com.example.demo.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/audit")
@Tag(name = "Audit Logs", description = "Audit trail and activity logs")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get audit logs", description = "Retrieve audit logs for organization")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<AuditLog> logs = auditService.getAuditLogs(organizationId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", logs));
    }

    @GetMapping("/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get entity audit history", description = "Get audit trail for specific entity")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getEntityAuditLogs(
            @PathVariable AuditLog.EntityType entityType,
            @PathVariable Long entityId,
            Pageable pageable) {
        Page<AuditLog> logs = auditService.getEntityAuditLogs(entityType, entityId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Entity audit logs retrieved successfully", logs));
    }
}
