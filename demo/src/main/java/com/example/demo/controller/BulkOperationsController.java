package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.service.BulkOperationsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bulk")
@Tag(name = "Bulk Operations", description = "Bulk create, update, delete operations")
@SecurityRequirement(name = "bearerAuth")
public class BulkOperationsController {

    @Autowired
    private BulkOperationsService bulkOperationsService;

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Bulk create products", description = "Create multiple products at once")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkCreateProducts(
            @Valid @RequestBody List<ProductRequest> requests) {
        Map<String, Object> result = bulkOperationsService.bulkCreateProducts(requests);
        return ResponseEntity.ok(ApiResponse.success("Bulk product creation completed", result));
    }

    @PutMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Bulk update products", description = "Update multiple products at once")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkUpdateProducts(
            @RequestBody List<Map<String, Object>> updates) {
        Map<String, Object> result = bulkOperationsService.bulkUpdateProducts(updates);
        return ResponseEntity.ok(ApiResponse.success("Bulk product update completed", result));
    }

    @DeleteMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Bulk delete products", description = "Delete multiple products at once")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkDeleteProducts(
            @RequestBody List<Long> productIds) {
        Map<String, Object> result = bulkOperationsService.bulkDeleteProducts(productIds);
        return ResponseEntity.ok(ApiResponse.success("Bulk product deletion completed", result));
    }

    @PutMapping("/products/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'STAFF')")
    @Operation(summary = "Bulk update stock", description = "Update stock quantities for multiple products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkUpdateStock(
            @RequestBody List<Map<String, Object>> stockUpdates) {
        Map<String, Object> result = bulkOperationsService.bulkUpdateStock(stockUpdates);
        return ResponseEntity.ok(ApiResponse.success("Bulk stock update completed", result));
    }
}
