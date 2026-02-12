package com.example.demo.dto;

import com.example.demo.enums.PlanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Organization request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 100, message = "Organization name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Company code is required")
    @Size(max = 50, message = "Company code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Company code must contain only uppercase letters, numbers, hyphens, and underscores")
    private String companyCode;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private PlanType planType;
}
