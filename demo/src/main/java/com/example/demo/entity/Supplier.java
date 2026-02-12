package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Supplier entity - represents external suppliers/vendors
 * Each supplier belongs to one organization and can supply multiple products
 * 
 * Design decisions:
 * - contactPerson for communication tracking
 * - leadTimeDays for supply chain planning
 * - minimumOrderQuantity (MOQ) for reorder logic
 * - rating/performance tracking for vendor management
 */
@Entity
@Table(name = "suppliers",
    indexes = {
        @Index(name = "idx_supplier_org", columnList = "organization_id"),
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_code", columnList = "supplier_code")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_supplier_org_code", columnNames = {"organization_id", "supplier_code"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "supplier_code", nullable = false, length = 50)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(length = 500)
    private String address;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    @Column(name = "minimum_order_quantity", nullable = false)
    private Integer minimumOrderQuantity;

    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "reliability_score", precision = 5, scale = 2)
    private BigDecimal reliabilityScore;

    // Many suppliers belong to one organization
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_supplier_organization"))
    private Organization organization;

    // One supplier supplies many products (many-to-many via SupplierProduct)
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SupplierProduct> suppliedProducts = new ArrayList<>();

    public void addSuppliedProduct(SupplierProduct supplierProduct) {
        suppliedProducts.add(supplierProduct);
        supplierProduct.setSupplier(this);
    }

    public void removeSuppliedProduct(SupplierProduct supplierProduct) {
        suppliedProducts.remove(supplierProduct);
        supplierProduct.setSupplier(null);
    }
}
