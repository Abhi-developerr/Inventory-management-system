package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem entity - represents individual items within an order
 * Links orders to products with quantity and price at time of order
 * 
 * Design decisions:
 * - price is captured at order time (historical price, not current product price)
 * - subtotal is denormalized for performance
 * - No direct organization_id (inherited through order)
 * - ManyToOne to Product with LAZY loading to avoid N+1 queries
 */
@Entity
@Table(name = "order_items",
    indexes = {
        @Index(name = "idx_order_item_order", columnList = "order_id"),
        @Index(name = "idx_order_item_product", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price at the time of order creation
     * This is important because product price may change later
     * We need to preserve the historical price for this order
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Calculated field: quantity * price
     * Denormalized for query performance
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Many order items belong to one order
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    // Many order items reference one product
    // LAZY loading: Product details loaded only when accessed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_order_item_product"))
    private Product product;

    /**
     * Calculate subtotal (quantity * price)
     */
    public void calculateSubtotal() {
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    @PrePersist
    @PreUpdate
    protected void calculateBeforeSave() {
        calculateSubtotal();
    }
}
