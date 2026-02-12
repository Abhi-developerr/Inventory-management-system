package com.example.demo.dto;

import com.example.demo.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Organization response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationResponse {

    private Long id;
    private String name;
    private String companyCode;
    private String description;
    private PlanType planType;
    private Boolean isActive;
    private Integer maxUsers;
    private Integer maxProducts;
    private Integer maxOrders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
