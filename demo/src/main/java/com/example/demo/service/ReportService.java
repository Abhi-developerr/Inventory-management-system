package com.example.demo.service;

import com.example.demo.dto.ReportRequest;
import com.example.demo.dto.ReportResponse;
import com.example.demo.entity.*;
import com.example.demo.enums.ReportType;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportScheduleRepository reportScheduleRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    /**
     * Generate a report based on type and filters
     */
    public ReportResponse generateReport(Long organizationId, ReportRequest request, String username) {
        try {
            Report report = Report.builder()
                    .name(request.name())
                    .description(request.description())
                    .reportType(request.reportType())
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .filterConfig(request.filterConfig())
                    .fileFormat(request.fileFormat() != null ? request.fileFormat() : "PDF")
                    .generatedAt(LocalDateTime.now())
                    .generatedBy(username)
                    .organization(Organization.builder().id(organizationId).build())
                    .build();

            // Generate report data based on type
            generateReportData(report);

            report = reportRepository.save(report);
            log.info("Report {} generated successfully", report.getId());

            return mapToResponse(report);
        } catch (Exception e) {
            log.error("Error generating report", e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Get all reports for organization
     */
    public Page<ReportResponse> getReports(Long organizationId, Pageable pageable) {
        return reportRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get report by ID
     */
    public ReportResponse getReportById(Long reportId, Long organizationId) {
        Report report = reportRepository.findByIdAndOrganizationId(reportId, organizationId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToResponse(report);
    }

    /**
     * Delete report
     */
    public void deleteReport(Long reportId, Long organizationId) {
        Report report = reportRepository.findByIdAndOrganizationId(reportId, organizationId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
        log.info("Report {} deleted", reportId);
    }

    /**
     * Get report data based on type
     */
    private void generateReportData(Report report) {
        switch (report.getReportType()) {
            case SALES_SUMMARY -> generateSalesSummary(report);
            case INVENTORY_STATUS -> generateInventoryStatus(report);
            case LOW_STOCK_ALERT -> generateLowStockAlert(report);
            case ORDER_ANALYSIS -> generateOrderAnalysis(report);
            case SUPPLIER_PERFORMANCE -> generateSupplierPerformance(report);
            case REVENUE_TREND -> generateRevenueTrend(report);
            case STOCK_MOVEMENT -> generateStockMovement(report);
            case PRODUCT_PERFORMANCE -> generateProductPerformance(report);
            default -> log.warn("Unknown report type: {}", report.getReportType());
        }
        report.setFileSize((long) (Math.random() * 1000000)); // Simulated file size
    }

    /**
     * Sales Summary Report
     */
    private void generateSalesSummary(Report report) {
        Long orgId = report.getOrganization().getId();
        LocalDate startDate = report.getStartDate() != null ? report.getStartDate() : LocalDate.now().minusMonths(1);
        LocalDate endDate = report.getEndDate() != null ? report.getEndDate() : LocalDate.now();

        // Find orders in date range
        List<Order> orders = orderRepository.findByOrganizationId(orgId, org.springframework.data.domain.Pageable.unpaged()).getContent().stream()
                .filter(o -> o.getCreatedAt().isAfter(startDate.atStartOfDay()) && o.getCreatedAt().isBefore(endDate.plusDays(1).atStartOfDay()))
                .toList();

        double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == com.example.demo.enums.OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();

        int totalOrders = orders.size();
        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        log.info("Sales Summary: Total Revenue=${}, Orders={}, Avg Order Value=${}", 
                 totalRevenue, totalOrders, avgOrderValue);
        report.setDescription(report.getDescription() + 
                "\n\nTotal Revenue: $" + String.format("%.2f", totalRevenue) +
                "\nTotal Orders: " + totalOrders +
                "\nAverage Order Value: $" + String.format("%.2f", avgOrderValue));
    }

    /**
     * Inventory Status Report
     */
    private void generateInventoryStatus(Report report) {
        Long orgId = report.getOrganization().getId();
        List<Product> products = productRepository.findByOrganizationId(orgId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        long totalItems = products.stream().mapToLong(Product::getStockQuantity).sum();
        long lowStockCount = products.stream()
                .filter(p -> p.getStockQuantity() < p.getLowStockThreshold())
                .count();
        long outOfStockCount = products.stream()
                .filter(p -> p.getStockQuantity() == 0)
                .count();

        log.info("Inventory Status: Total Items={}, Low Stock={}, Out of Stock={}", 
                 totalItems, lowStockCount, outOfStockCount);
        report.setDescription(report.getDescription() +
                "\n\nTotal Stock Items: " + totalItems +
                "\nLow Stock Products: " + lowStockCount +
                "\nOut of Stock Products: " + outOfStockCount);
    }

    /**
     * Low Stock Alert Report
     */
    private void generateLowStockAlert(Report report) {
        Long orgId = report.getOrganization().getId();
        List<Product> lowStockProducts = productRepository.findLowStockProducts(orgId);

        log.info("Low Stock Alert: {} products below threshold", lowStockProducts.size());
        report.setDescription(report.getDescription() +
                "\n\nProducts Below Threshold: " + lowStockProducts.size() +
                "\nRecommended Reorder Quantity: Calculated per product");
    }

    /**
     * Order Analysis Report
     */
    private void generateOrderAnalysis(Report report) {
        Long orgId = report.getOrganization().getId();
        List<Order> orders = orderRepository.findByOrganizationId(orgId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        Map<String, Long> statusCounts = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().toString(), Collectors.counting()));

        log.info("Order Analysis: Status Distribution={}", statusCounts);
        report.setDescription(report.getDescription() +
                "\n\nTotal Orders: " + orders.size() +
                "\nOrder Status Distribution: " + statusCounts);
    }

    /**
     * Supplier Performance Report
     */
    private void generateSupplierPerformance(Report report) {
        Long orgId = report.getOrganization().getId();
        List<Supplier> suppliers = supplierRepository.findByOrganizationId(orgId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        double avgReliability = suppliers.stream()
                .mapToDouble(s -> s.getReliabilityScore() != null ? s.getReliabilityScore().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        log.info("Supplier Performance: {} suppliers, Avg Reliability Score={}", 
                 suppliers.size(), avgReliability);
        report.setDescription(report.getDescription() +
                "\n\nTotal Suppliers: " + suppliers.size() +
                "\nAverage Reliability Score: " + avgReliability);
    }

    /**
     * Revenue Trend Report (monthly)
     */
    private void generateRevenueTrend(Report report) {
        Long orgId = report.getOrganization().getId();
        log.info("Revenue Trend Report generated for org {}", orgId);
        report.setDescription(report.getDescription() +
                "\n\nRevenue Trend: Monthly breakdown over selected period");
    }

    /**
     * Stock Movement Report
     */
    private void generateStockMovement(Report report) {
        Long orgId = report.getOrganization().getId();
        log.info("Stock Movement Report generated for org {}", orgId);
        report.setDescription(report.getDescription() +
                "\n\nStock Movement: Inbound and outbound transactions");
    }

    /**
     * Product Performance Report
     */
    private void generateProductPerformance(Report report) {
        Long orgId = report.getOrganization().getId();
        List<Product> products = productRepository.findByOrganizationId(orgId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        List<Product> topProducts = products.stream()
                .sorted(Comparator.comparingLong(Product::getStockQuantity).reversed())
                .limit(10)
                .toList();

        log.info("Product Performance: Top {} products", topProducts.size());
        report.setDescription(report.getDescription() +
                "\n\nTop Performing Products: " + topProducts.size() + " identified");
    }

    /**
     * Schedule a report for recurring generation
     */
    public void scheduleReport(Long reportId, Long organizationId, String frequency, String emailRecipients) {
        Report report = reportRepository.findByIdAndOrganizationId(reportId, organizationId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        ReportSchedule schedule = ReportSchedule.builder()
                .report(report)
                .organization(report.getOrganization())
                .frequency(com.example.demo.enums.ReportFrequency.valueOf(frequency))
                .emailRecipients(emailRecipients)
                .isActive(true)
                .nextRunAt(calculateNextRunTime(frequency))
                .build();

        reportScheduleRepository.save(schedule);
        log.info("Report {} scheduled for {} delivery", reportId, frequency);
    }

    /**
     * Calculate next run time based on frequency
     */
    private LocalDateTime calculateNextRunTime(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        return switch (frequency) {
            case "DAILY" -> now.plusDays(1);
            case "WEEKLY" -> now.plusWeeks(1);
            case "MONTHLY" -> now.plusMonths(1);
            case "QUARTERLY" -> now.plusMonths(3);
            case "YEARLY" -> now.plusYears(1);
            default -> now.plusDays(1);
        };
    }

    /**
     * Map Report to ReportResponse DTO
     */
    private ReportResponse mapToResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getName(),
                report.getDescription(),
                report.getReportType(),
                report.getStartDate(),
                report.getEndDate(),
                report.getFileFormat(),
                report.getGeneratedAt(),
                report.getGeneratedBy(),
                report.getFileSize(),
                report.getIsTemplate(),
                report.getFileName()
        );
    }
}
