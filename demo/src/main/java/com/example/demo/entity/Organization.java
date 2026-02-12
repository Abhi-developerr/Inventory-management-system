package com.example.demo.entity;

import com.example.demo.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Organization entity - represents a tenant in the multi-tenant system
 * Each organization is completely isolated from others
 * 
 * Design decisions:
 * - Soft delete pattern (isActive flag) to preserve historical data
 * - Plan-based feature limits for SaaS monetization
 * - Lazy loading of users/products to avoid N+1 queries
 */
@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_org_name", columnList = "name"),
    @Index(name = "idx_org_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "company_code", unique = true, nullable = false, length = 50)
    private String companyCode;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_products")
    private Integer maxProducts;

    @Column(name = "max_orders")
    private Integer maxOrders;

    // One organization has many users
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // One organization has many categories
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    // One organization has many products
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    // One organization has many orders
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();
}
