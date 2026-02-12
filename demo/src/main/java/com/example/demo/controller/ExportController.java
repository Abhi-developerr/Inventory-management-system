package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.SecurityUtils;
import com.example.demo.service.ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/export")
@Tag(name = "Export", description = "Data export functionality")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/products/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Export products to Excel", description = "Download all products as Excel file")
    public ResponseEntity<byte[]> exportProductsToExcel() throws IOException {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        byte[] excelData = exportService.exportProductsToExcel(organizationId);

        String filename = "products_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/products/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Export products to CSV", description = "Download all products as CSV file")
    public ResponseEntity<byte[]> exportProductsToCSV() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        byte[] csvData = exportService.exportProductsToCSV(organizationId);

        String filename = "products_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/orders/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Export orders to Excel", description = "Download all orders as Excel file")
    public ResponseEntity<byte[]> exportOrdersToExcel() throws IOException {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        byte[] excelData = exportService.exportOrdersToExcel(organizationId);

        String filename = "orders_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
}
