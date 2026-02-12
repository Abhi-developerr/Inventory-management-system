package com.example.demo.repository;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Page<PurchaseOrder> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<PurchaseOrder> findByOrganizationIdAndStatus(Long organizationId, PurchaseOrderStatus status, Pageable pageable);

    Optional<PurchaseOrder> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    List<PurchaseOrder> findByOrganizationIdAndStatus(Long organizationId, PurchaseOrderStatus status);

    long countByOrganizationId(Long organizationId);

    long countByOrganizationIdAndStatus(Long organizationId, PurchaseOrderStatus status);
}
