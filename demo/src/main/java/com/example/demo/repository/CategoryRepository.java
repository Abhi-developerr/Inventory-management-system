package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories by organization (tenant-specific)
     */
    List<Category> findByOrganizationId(Long organizationId);

    /**
     * Find categories by organization with pagination
     */
    Page<Category> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Find active categories by organization
     */
    List<Category> findByOrganizationIdAndIsActiveTrue(Long organizationId);

    /**
     * Find category by ID and organization (ensures tenant isolation)
     */
    Optional<Category> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Check if category name exists in organization
     */
    boolean existsByNameAndOrganizationId(String name, Long organizationId);

    /**
     * Count categories in organization
     */
    long countByOrganizationId(Long organizationId);
}
