package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by organization
     */
    Page<Product> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Find all products by organization (no pagination)
     */
    List<Product> findAllByOrganizationId(Long organizationId);

    /**
     * Find product by ID and organization (tenant isolation)
     */
    Optional<Product> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Find product by SKU and organization
     */
    Optional<Product> findBySkuAndOrganizationId(String sku, Long organizationId);

    /**
     * Find product by barcode and organization
     */
    Optional<Product> findByBarcodeNumberAndOrganizationId(String barcodeNumber, Long organizationId);

    /**
     * Find products by category
     */
    Page<Product> findByOrganizationIdAndCategoryId(Long organizationId, Long categoryId, Pageable pageable);

    /**
     * Find active products
     */
    Page<Product> findByOrganizationIdAndIsActiveTrue(Long organizationId, Pageable pageable);

    /**
     * Find low stock products (stock <= threshold)
     */
    @Query("SELECT p FROM Product p WHERE p.organization.id = :orgId " +
           "AND p.stockQuantity <= p.lowStockThreshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("orgId") Long organizationId);

    /**
     * Search products by name, SKU, or description
     */
    @Query("SELECT p FROM Product p WHERE p.organization.id = :orgId " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("orgId") Long organizationId,
                                 @Param("search") String search,
                                 Pageable pageable);

    /**
     * Check if SKU exists in organization
     */
    boolean existsBySkuAndOrganizationId(String sku, Long organizationId);

    /**
     * Count products in organization
     */
    long countByOrganizationId(Long organizationId);

    /**
     * Count active products in organization
     */
    long countByOrganizationIdAndIsActiveTrue(Long organizationId);

    /**
     * Count low stock products
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.organization.id = :orgId " +
           "AND p.stockQuantity <= p.lowStockThreshold AND p.isActive = true")
    long countLowStockProducts(@Param("orgId") Long organizationId);
}
