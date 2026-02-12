package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ReportRequest;
import com.example.demo.dto.ReportResponse;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate a new report
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(
            @RequestBody ReportRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        // Get organization from auth context (would be added to UserPrincipal)
        Long organizationId = 1L; // TODO: Extract from auth context
        
        ReportResponse response = reportService.generateReport(organizationId, request, username);
        return ResponseEntity.ok(ApiResponse.success("Report generated successfully", response));
    }

    /**
     * Get all reports for organization
     */
    @GetMapping
    public ResponseEntity<?> getReports(
            Pageable pageable,
            Authentication authentication) {
        Long organizationId = 1L; // TODO: Extract from auth context
        Page<ReportResponse> reports = reportService.getReports(organizationId, pageable);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get report by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(
            @PathVariable Long id,
            Authentication authentication) {
        Long organizationId = 1L; // TODO: Extract from auth context
        ReportResponse response = reportService.getReportById(id, organizationId);
        return ResponseEntity.ok(ApiResponse.success("Report retrieved", response));
    }

    /**
     * Delete report
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long id,
            Authentication authentication) {
        Long organizationId = 1L; // TODO: Extract from auth context
        reportService.deleteReport(id, organizationId);
        return ResponseEntity.ok(ApiResponse.successMessage("Report deleted successfully"));
    }

    /**
     * Schedule report for recurring generation
     */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<?> scheduleReport(
            @PathVariable Long id,
            @RequestParam String frequency,
            @RequestParam String emailRecipients,
            Authentication authentication) {
        Long organizationId = 1L; // TODO: Extract from auth context
        reportService.scheduleReport(id, organizationId, frequency, emailRecipients);
        return ResponseEntity.ok(ApiResponse.successMessage("Report scheduled successfully"));
    }
}
