package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponse {

    private Long id;

    private String name;

    private String code;

    private String description;

    private String contactPerson;

    private String contactEmail;

    private String contactPhone;

    private String address;

    private Integer leadTimeDays;

    private Integer minimumOrderQuantity;

    private String paymentTerms;

    private Boolean isActive;

    private BigDecimal rating;

    private BigDecimal reliabilityScore;

    private Long organizationId;

    private List<SupplierProductResponse> products;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierProductResponse {

        private Long id;

        private Long productId;

        private String productName;

        private String productSku;

        private String supplierSku;

        private BigDecimal costPrice;

        private Boolean isAvailable;

        private String notes;
    }
}
