package com.example.demo.service;

import com.example.demo.dto.OrganizationRequest;
import com.example.demo.dto.OrganizationResponse;
import com.example.demo.entity.Organization;
import com.example.demo.enums.PlanType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Organization Service - Business logic for organization management
 * Only accessible by SUPER_ADMIN
 */
@Service
@Transactional
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Get all organizations with pagination
     */
    public Page<OrganizationResponse> getAllOrganizations(Pageable pageable) {
        Page<Organization> organizations = organizationRepository.findAll(pageable);
        return organizations.map(this::toResponse);
    }

    /**
     * Get organization by ID
     */
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
        return toResponse(organization);
    }

    /**
     * Create new organization
     */
    public OrganizationResponse createOrganization(OrganizationRequest request) {
        // Check for duplicate company code
        if (organizationRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new BadRequestException("Organization with company code '" + request.getCompanyCode() + "' already exists");
        }

        // Check for duplicate name
        if (organizationRepository.existsByName(request.getName())) {
            throw new BadRequestException("Organization with name '" + request.getName() + "' already exists");
        }

        Organization organization = Organization.builder()
                .name(request.getName())
                .companyCode(request.getCompanyCode())
                .description(request.getDescription())
                .planType(request.getPlanType() != null ? request.getPlanType() : PlanType.BASIC)
                .isActive(true)
                .build();

        // Set plan limits based on plan type
        setPlanLimits(organization);

        organization = organizationRepository.save(organization);
        return toResponse(organization);
    }

    /**
     * Update organization
     */
    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

        // Check for duplicate company code (excluding current organization)
        if (!organization.getCompanyCode().equals(request.getCompanyCode()) &&
                organizationRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new BadRequestException("Organization with company code '" + request.getCompanyCode() + "' already exists");
        }

        // Check for duplicate name (excluding current organization)
        if (!organization.getName().equals(request.getName()) &&
                organizationRepository.existsByName(request.getName())) {
            throw new BadRequestException("Organization with name '" + request.getName() + "' already exists");
        }

        organization.setName(request.getName());
        organization.setCompanyCode(request.getCompanyCode());
        organization.setDescription(request.getDescription());
        
        // Update plan type if changed
        if (request.getPlanType() != null && !organization.getPlanType().equals(request.getPlanType())) {
            organization.setPlanType(request.getPlanType());
            setPlanLimits(organization);
        }

        organization = organizationRepository.save(organization);
        return toResponse(organization);
    }

    /**
     * Delete organization
     */
    public void deleteOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
        
        // Soft delete
        organization.setIsActive(false);
        organizationRepository.save(organization);
    }

    /**
     * Toggle organization active status
     */
    public OrganizationResponse toggleOrganizationStatus(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
        
        organization.setIsActive(!organization.getIsActive());
        organization = organizationRepository.save(organization);
        return toResponse(organization);
    }

    /**
     * Set plan limits based on plan type
     */
    private void setPlanLimits(Organization organization) {
        switch (organization.getPlanType()) {
            case FREE:
                organization.setMaxUsers(3);
                organization.setMaxProducts(50);
                organization.setMaxOrders(200);
                break;
            case BASIC:
                organization.setMaxUsers(5);
                organization.setMaxProducts(100);
                organization.setMaxOrders(500);
                break;
            case PREMIUM:
                organization.setMaxUsers(20);
                organization.setMaxProducts(1000);
                organization.setMaxOrders(5000);
                break;
            case ENTERPRISE:
                organization.setMaxUsers(null); // Unlimited
                organization.setMaxProducts(null);
                organization.setMaxOrders(null);
                break;
        }
    }

    /**
     * Convert entity to response DTO
     */
    private OrganizationResponse toResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .companyCode(organization.getCompanyCode())
                .description(organization.getDescription())
                .planType(organization.getPlanType())
                .isActive(organization.getIsActive())
                .maxUsers(organization.getMaxUsers())
                .maxProducts(organization.getMaxProducts())
                .maxOrders(organization.getMaxOrders())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
}
