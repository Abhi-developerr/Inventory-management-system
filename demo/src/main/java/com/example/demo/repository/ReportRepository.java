package com.example.demo.repository;

import com.example.demo.entity.Report;
import com.example.demo.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId, Pageable pageable);

    List<Report> findByOrganizationIdAndReportType(Long organizationId, ReportType reportType);

    Optional<Report> findByIdAndOrganizationId(Long id, Long organizationId);

    @Query("SELECT r FROM Report r WHERE r.organization.id = ?1 AND r.isTemplate = true")
    List<Report> findTemplatesByOrganizationId(Long organizationId);

    @Query("SELECT r FROM Report r WHERE r.organization.id = ?1 AND r.reportType = ?2 " +
           "AND r.generatedAt >= ?3 ORDER BY r.generatedAt DESC")
    List<Report> findRecentReportsByType(Long organizationId, ReportType type, LocalDate since);
}
