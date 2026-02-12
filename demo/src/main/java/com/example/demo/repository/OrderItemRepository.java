package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all order items by order ID
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find order items by product
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId " +
           "AND oi.order.organization.id = :orgId")
    List<OrderItem> findByProductAndOrganization(@Param("productId") Long productId,
                                                  @Param("orgId") Long organizationId);
}
