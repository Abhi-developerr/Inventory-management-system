package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrganizationRequest;
import com.example.demo.dto.OrganizationResponse;
import com.example.demo.service.OrganizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Organization Management Controller
 * Only accessible by SUPER_ADMIN
 */
@RestController
@RequestMapping("/organizations")
@Tag(name = "Organizations", description = "Organization management endpoints for SUPER_ADMIN")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * Get all organizations (SUPER_ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all organizations", description = "Retrieve all organizations (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Page<OrganizationResponse>>> getAllOrganizations(Pageable pageable) {
        Page<OrganizationResponse> organizations = organizationService.getAllOrganizations(pageable);
        return ResponseEntity.ok(ApiResponse.success("Organizations retrieved successfully", organizations));
    }

    /**
     * Get organization by ID (SUPER_ADMIN only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get organization by ID", description = "Retrieve organization details (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationById(@PathVariable Long id) {
        OrganizationResponse organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(ApiResponse.success("Organization retrieved successfully", organization));
    }

    /**
     * Create organization (SUPER_ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create organization", description = "Create new organization (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(@Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse organization = organizationService.createOrganization(request);
        return ResponseEntity.ok(ApiResponse.success("Organization created successfully", organization));
    }

    /**
     * Update organization (SUPER_ADMIN only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update organization", description = "Update organization details (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {
        
        OrganizationResponse organization = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(ApiResponse.success("Organization updated successfully", organization));
    }

    /**
     * Delete organization (SUPER_ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete organization", description = "Soft delete organization (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Organization deleted successfully"));
    }

    /**
     * Toggle organization status (SUPER_ADMIN only)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Toggle organization status", description = "Activate/deactivate organization (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> toggleOrganizationStatus(@PathVariable Long id) {
        OrganizationResponse organization = organizationService.toggleOrganizationStatus(id);
        
        String message = organization.getIsActive() ? 
            "Organization activated successfully" : "Organization deactivated successfully";
        
        return ResponseEntity.ok(ApiResponse.success(message, organization));
    }
}
