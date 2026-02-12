package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.util.SecurityUtils;
import com.example.demo.service.AnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Business intelligence and reporting")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Get dashboard analytics", description = "Comprehensive dashboard metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardAnalytics() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Map<String, Object> analytics = analyticsService.getDashboardAnalytics(organizationId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard analytics retrieved successfully", analytics));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Get top selling products", description = "Products ranked by sales volume")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        List<Map<String, Object>> topProducts = analyticsService.getTopSellingProducts(organizationId, limit);
        return ResponseEntity.ok(ApiResponse.success("Top products retrieved successfully", topProducts));
    }

    @GetMapping("/sales-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Get sales trend", description = "Daily sales for specified period")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSalesTrend(
            @RequestParam(defaultValue = "30") int days) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        List<Map<String, Object>> trend = analyticsService.getSalesTrend(organizationId, days);
        return ResponseEntity.ok(ApiResponse.success("Sales trend retrieved successfully", trend));
    }

    @GetMapping("/forecast/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get inventory forecast", description = "AI-powered inventory forecasting")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryForecast(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "14") int daysToForecast) {
        Map<String, Object> forecast = analyticsService.getInventoryForecast(productId, daysToForecast);
        return ResponseEntity.ok(ApiResponse.success("Forecast generated successfully", forecast));
    }

    @GetMapping("/low-stock-alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Get low stock alerts", description = "Products requiring attention")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLowStockAlerts() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        List<Map<String, Object>> alerts = analyticsService.getLowStockAlerts(organizationId);
        return ResponseEntity.ok(ApiResponse.success("Low stock alerts retrieved successfully", alerts));
    }

    @GetMapping("/order-status-breakdown")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Get order status breakdown", description = "Orders grouped by status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getOrderStatusBreakdown() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Map<String, Long> breakdown = analyticsService.getOrderStatusBreakdown(organizationId);
        return ResponseEntity.ok(ApiResponse.success("Order breakdown retrieved successfully", breakdown));
    }
}
