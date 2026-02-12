package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SupplierRequest;
import com.example.demo.dto.SupplierResponse;
import com.example.demo.service.SupplierService;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/suppliers")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers/vendors")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all suppliers", description = "Retrieve paginated list of active suppliers")
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> getAllSuppliers(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<SupplierResponse> suppliers = supplierService.getAllSuppliers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully", suppliers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get supplier by ID", description = "Retrieve specific supplier with linked products")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
        SupplierResponse supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved successfully", supplier));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search suppliers", description = "Search suppliers by name, code, or contact person")
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> searchSuppliers(
            @RequestParam String query,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<SupplierResponse> suppliers = supplierService.searchSuppliers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", suppliers));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create supplier", description = "Create a new supplier (Admin only)")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody SupplierRequest request) {
        
        SupplierResponse supplier = supplierService.createSupplier(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", supplier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update supplier", description = "Update existing supplier (Admin only)")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        
        SupplierResponse supplier = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", supplier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete supplier", description = "Soft delete supplier (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Supplier deleted successfully"));
    }

    @PostMapping("/{supplierId}/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Link product to supplier", description = "Link a product and associate cost price (Admin only)")
    public ResponseEntity<ApiResponse<Void>> linkProductToSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long productId,
            @RequestParam BigDecimal costPrice,
            @RequestParam(required = false) String supplierSku) {
        
        supplierService.linkProductToSupplier(supplierId, productId, costPrice, supplierSku);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successMessage("Product linked to supplier successfully"));
    }

    @DeleteMapping("/{supplierId}/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Unlink product from supplier", description = "Remove product-supplier link (Admin only)")
    public ResponseEntity<ApiResponse<Void>> unlinkProductFromSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long productId) {
        
        supplierService.unlinkProductFromSupplier(supplierId, productId);
        return ResponseEntity.ok(ApiResponse.successMessage("Product unlinked from supplier successfully"));
    }
}
