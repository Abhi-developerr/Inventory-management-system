package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Supplier code is required")
    private String code;

    private String description;

    private String contactPerson;

    @Email(message = "Contact email must be valid")
    private String contactEmail;

    private String contactPhone;

    private String address;

    @Min(value = 1, message = "Lead time must be at least 1 day")
    private Integer leadTimeDays;

    @Min(value = 1, message = "MOQ must be at least 1")
    private Integer minimumOrderQuantity;

    private String paymentTerms;

    private BigDecimal rating;

    private BigDecimal reliabilityScore;
}
