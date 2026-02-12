package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Category entity - represents product categories
 * Each category belongs to one organization (tenant-specific)
 * 
 * Design decisions:
 * - Categories are organization-specific (not shared across tenants)
 * - Composite unique constraint on (organization_id, name) prevents duplicates within org
 * - Lazy loading of products (can be many)
 * - Soft delete via organization isActive check
 */
@Entity
@Table(name = "categories",
    indexes = {
        @Index(name = "idx_category_org", columnList = "organization_id"),
        @Index(name = "idx_category_name", columnList = "name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_org_name", 
                         columnNames = {"organization_id", "name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Many categories belong to one organization
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_category_organization"))
    private Organization organization;

    // One category has many products
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Product> products = new HashSet<>();
}
