package com.example.demo.repository;

import com.example.demo.entity.ReportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule, Long> {

    Optional<ReportSchedule> findByReportId(Long reportId);

    List<ReportSchedule> findByOrganizationIdAndIsActiveTrue(Long organizationId);

    @Query("SELECT rs FROM ReportSchedule rs WHERE rs.isActive = true AND rs.nextRunAt <= NOW()")
    List<ReportSchedule> findScheduledForExecution();
}
