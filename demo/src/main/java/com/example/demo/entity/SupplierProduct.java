package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * SupplierProduct junction entity - many-to-many relationship between Supplier and Product
 * Tracks supplier-specific product information like cost price and availability
 */
@Entity
@Table(name = "supplier_products",
    indexes = {
        @Index(name = "idx_sup_prod_supplier", columnList = "supplier_id"),
        @Index(name = "idx_sup_prod_product", columnList = "product_id"),
        @Index(name = "idx_sup_prod_supplier_product", columnList = "supplier_id, product_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_supplier_product", columnNames = {"supplier_id", "product_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sup_prod_supplier"))
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sup_prod_product"))
    private Product product;

    @Column(name = "supplier_sku", length = 100)
    private String supplierSku;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "notes", length = 500)
    private String notes;
}
