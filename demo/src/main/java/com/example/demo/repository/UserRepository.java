package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository - data access layer for User entity
 * Spring Data JPA auto-implements basic CRUD operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (for authentication)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by organization (tenant-specific)
     */
    List<User> findByOrganizationId(Long organizationId);

    /**
     * Find users by organization with pagination
     */
    Page<User> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Find users by organization and role
     */
    List<User> findByOrganizationIdAndRole(Long organizationId, Role role);

    /**
     * Find active users in organization
     */
    List<User> findByOrganizationIdAndIsActiveTrue(Long organizationId);

    /**
     * Count users in organization
     */
    long countByOrganizationId(Long organizationId);

    /**
     * Count active users in organization
     */
    long countByOrganizationIdAndIsActiveTrue(Long organizationId);

    /**
     * Search users by name or email within organization
     */
    @Query("SELECT u FROM User u WHERE u.organization.id = :orgId " +
           "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsersByOrganization(@Param("orgId") Long organizationId,
                                         @Param("search") String search,
                                         Pageable pageable);
}
