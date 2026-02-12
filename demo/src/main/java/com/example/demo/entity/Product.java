package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products",
    indexes = {
        @Index(name = "idx_product_org", columnList = "organization_id"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_sku", columnList = "sku"),
        @Index(name = "idx_product_barcode", columnList = "barcode_number"),
        @Index(name = "idx_product_org_sku", columnList = "organization_id, sku"),
        @Index(name = "idx_product_org_stock", columnList = "organization_id, stock_quantity")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_org_sku", 
                         columnNames = {"organization_id", "sku"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = false, length = 50)
    private String sku; // Stock Keeping Unit

    @Column(name = "barcode_number", length = 100)
    private String barcodeNumber; // EAN-13, UPC, QR code, etc.

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Version
    @Column(name = "version")
    private Long version;

    // Many products belong to one organization
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_product_organization"))
    private Organization organization;

    // Many products belong to one category
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    public boolean isLowStock() {
        return stockQuantity <= lowStockThreshold;
    }

    /**
     * Business logic: Check if sufficient stock available
     */
    public boolean hasStock(Integer quantity) {
        return stockQuantity >= quantity;
    }

    /**
     * Business logic: Reduce stock (should be called within @Transactional)
     */
    public void reduceStock(Integer quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException(
                String.format("Insufficient stock for product %s. Available: %d, Required: %d", 
                             name, stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Business logic: Restore stock (e.g., when order is cancelled)
     */
    public void restoreStock(Integer quantity) {
        this.stockQuantity += quantity;
    }
}
