package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PurchaseOrderRequest;
import com.example.demo.dto.PurchaseOrderResponse;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@Tag(name = "Purchase Order Management", description = "APIs for managing purchase orders and automated reordering")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all purchase orders", description = "Retrieve paginated list of purchase orders")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getAllPurchaseOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<PurchaseOrderResponse> pos = purchaseOrderService.getAllPurchaseOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders retrieved successfully", pos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get PO by ID", description = "Retrieve specific purchase order with line items")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderResponse po = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order retrieved successfully", po));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get POs by status", description = "Retrieve purchase orders filtered by status")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getPurchaseOrdersByStatus(
            @PathVariable PurchaseOrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<PurchaseOrderResponse> pos = purchaseOrderService.getPurchaseOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders retrieved successfully", pos));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create purchase order", description = "Create a new purchase order (Admin only)")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {
        
        PurchaseOrderResponse po = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase order created successfully", po));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Approve purchase order", description = "Approve a draft/pending PO (Admin only)")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> approvePurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponse po = purchaseOrderService.approvePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order approved successfully", po));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Mark PO as received", description = "Mark purchase order as received and restore stock (Admin only)")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponse po = purchaseOrderService.receivePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order marked as received", po));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Cancel purchase order", description = "Cancel a purchase order (Admin only)")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> cancelPurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponse po = purchaseOrderService.cancelPurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order cancelled successfully", po));
    }
}
