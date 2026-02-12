package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByOrganizationIdOrderByTimestampDesc(Long organizationId, Pageable pageable);

    Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    Page<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            AuditLog.EntityType entityType, Long entityId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = ?1 AND a.timestamp BETWEEN ?2 AND ?3 ORDER BY a.timestamp DESC")
    List<AuditLog> findByOrganizationIdAndTimestampBetween(
            Long organizationId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = ?1 AND a.action = ?2 ORDER BY a.timestamp DESC")
    Page<AuditLog> findByOrganizationIdAndAction(
            Long organizationId, AuditLog.ActionType action, Pageable pageable);
}
