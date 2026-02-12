package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders by organization
     */
    Page<Order> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Find all orders by organization (no pagination)
     */
    List<Order> findAllByOrganizationId(Long organizationId);

    /**
     * Find order by ID and organization
     */
    Optional<Order> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Find orders by status
     */
    Page<Order> findByOrganizationIdAndStatus(Long organizationId, OrderStatus status, Pageable pageable);

    /**
     * Find orders by user
     */
    Page<Order> findByOrganizationIdAndUserId(Long organizationId, Long userId, Pageable pageable);

    /**
     * Find orders by date range
     */
    @Query("SELECT o FROM Order o WHERE o.organization.id = :orgId " +
           "AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findOrdersByDateRange(@Param("orgId") Long organizationId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);

    /**
     * Search orders by order number or customer name
     */
    @Query("SELECT o FROM Order o WHERE o.organization.id = :orgId " +
           "AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> searchOrders(@Param("orgId") Long organizationId,
                             @Param("search") String search,
                             Pageable pageable);

    /**
     * Count orders by organization
     */
    long countByOrganizationId(Long organizationId);

    /**
     * Count orders by status
     */
    long countByOrganizationIdAndStatus(Long organizationId, OrderStatus status);

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);
}
