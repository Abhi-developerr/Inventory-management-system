package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

/**
 * Export Service
 * Handles data export to CSV/Excel formats
 */
@Service
public class ExportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Export products to Excel
     */
    public byte[] exportProductsToExcel(Long organizationId) throws IOException {
        List<Product> products = productRepository.findAllByOrganizationId(organizationId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "SKU", "Name", "Description", "Category", 
                               "Price", "Quantity", "Min Stock", "Created At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Fill data rows
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getSku());
                row.createCell(2).setCellValue(product.getName());
                row.createCell(3).setCellValue(product.getDescription());
                row.createCell(4).setCellValue(product.getCategory() != null ? 
                                               product.getCategory().getName() : "N/A");
                row.createCell(5).setCellValue(product.getPrice().doubleValue());
                row.createCell(6).setCellValue(product.getStockQuantity());
                row.createCell(7).setCellValue(product.getLowStockThreshold());
                row.createCell(8).setCellValue(product.getCreatedAt().format(formatter));
            }

            // Auto-size all columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Export orders to Excel
     */
    public byte[] exportOrdersToExcel(Long organizationId) throws IOException {
        List<Order> orders = orderRepository.findAllByOrganizationId(organizationId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Orders");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Order ID", "Order Number", "Customer Name", "Customer Email",
                               "Status", "Total Amount", "Items", "Created At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getOrderNumber());
                row.createCell(2).setCellValue(order.getCustomerName());
                row.createCell(3).setCellValue(order.getCustomerEmail());
                row.createCell(4).setCellValue(order.getStatus().name());
                row.createCell(5).setCellValue(order.getTotalAmount().doubleValue());
                row.createCell(6).setCellValue(order.getOrderItems().size());
                row.createCell(7).setCellValue(order.getCreatedAt().format(formatter));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Create order items sheet
            Sheet itemsSheet = workbook.createSheet("Order Items");
            Row itemsHeaderRow = itemsSheet.createRow(0);
            String[] itemsHeaders = {"Order Number", "Product Name", "SKU", 
                                    "Quantity", "Unit Price", "Subtotal"};
            
            for (int i = 0; i < itemsHeaders.length; i++) {
                Cell cell = itemsHeaderRow.createCell(i);
                cell.setCellValue(itemsHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int itemRowNum = 1;
            for (Order order : orders) {
                for (OrderItem item : order.getOrderItems()) {
                    Row row = itemsSheet.createRow(itemRowNum++);
                    row.createCell(0).setCellValue(order.getOrderNumber());
                    row.createCell(1).setCellValue(item.getProduct().getName());
                    row.createCell(2).setCellValue(item.getProduct().getSku());
                    row.createCell(3).setCellValue(item.getQuantity());
                    row.createCell(4).setCellValue(item.getPrice().doubleValue());
                    row.createCell(5).setCellValue(item.getSubtotal().doubleValue());
                }
            }

            for (int i = 0; i < itemsHeaders.length; i++) {
                itemsSheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Export products to CSV
     */
    public byte[] exportProductsToCSV(Long organizationId) {
        List<Product> products = productRepository.findAllByOrganizationId(organizationId);
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("ID,SKU,Name,Description,Category,Price,Quantity,Min Stock,Created At\n");
        
        // Data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Product product : products) {
            csv.append(String.format("%d,%s,\"%s\",\"%s\",%s,%.2f,%d,%d,%s\n",
                product.getId(),
                product.getSku(),
                product.getName().replace("\"", "\"\""),
                product.getDescription() != null ? product.getDescription().replace("\"", "\"\"") : "",
                product.getCategory() != null ? product.getCategory().getName() : "N/A",
                product.getPrice().doubleValue(),
                product.getStockQuantity(),
                product.getLowStockThreshold(),
                product.getCreatedAt().format(formatter)
            ));
        }
        
        return csv.toString().getBytes();
    }
}
