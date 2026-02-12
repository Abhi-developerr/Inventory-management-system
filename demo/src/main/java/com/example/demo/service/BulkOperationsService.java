package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ProductRequest;
import com.example.demo.entity.AuditLog;
import com.example.demo.entity.Category;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.util.SecurityUtils;

/**
 * Bulk Operations Service
 * Handles bulk create, update, delete operations with notifications
 */
@Service
public class BulkOperationsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Bulk create products
     */
    @Transactional
    public Map<String, Object> bulkCreateProducts(List<ProductRequest> requests) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Organization organization = new Organization();
        organization.setId(organizationId);

        List<Product> products = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            final int rowNumber = i + 1;
            ProductRequest request = requests.get(i);
            try {
                // Validate SKU uniqueness
                if (productRepository.existsBySkuAndOrganizationId(request.getSku(), organizationId)) {
                    errors.add(String.format("Row %d: SKU '%s' already exists", rowNumber, request.getSku()));
                    continue;
                }

                // Get category
                Category category = categoryRepository.findByIdAndOrganizationId(request.getCategoryId(), organizationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found at row " + rowNumber));

                Product product = Product.builder()
                        .name(request.getName())
                        .sku(request.getSku())
                        .description(request.getDescription())
                        .price(request.getPrice())
                        .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                        .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
                        .category(category)
                        .organization(organization)
                        .isActive(true)
                        .build();

                products.add(product);
                successCount++;
            } catch (Exception e) {
                errors.add(String.format("Row %d: %s", rowNumber, e.getMessage()));
            }
        }

        // Save all valid products
        if (!products.isEmpty()) {
            productRepository.saveAll(products);
            
            // Create audit log
            auditService.log(
                AuditLog.ActionType.BULK_CREATE,
                AuditLog.EntityType.PRODUCT,
                null,
                null,
                successCount + " products created",
                "Bulk product creation completed"
            );

            // Create notification would require user entity, skip for now
            /* notificationService.createNotification(
                user,
                organization,
                com.example.demo.entity.Notification.NotificationType.BULK_OPERATION_COMPLETE,
                "Bulk Product Creation Complete",
                String.format("Successfully created %d products", successCount),
                com.example.demo.entity.Notification.Priority.MEDIUM,
                null,
                null
            ); */
        }

        return Map.of(
            "success", successCount,
            "failed", errors.size(),
            "total", requests.size(),
            "errors", errors
        );
    }

    /**
     * Bulk update products
     */
    @Transactional
    public Map<String, Object> bulkUpdateProducts(List<Map<String, Object>> updates) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < updates.size(); i++) {
            final int rowNumber = i + 1;
            try {
                Map<String, Object> update = updates.get(i);
                Long productId = Long.valueOf(update.get("id").toString());

                Product product = productRepository.findByIdAndOrganizationId(productId, organizationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found at row " + rowNumber));

                // Update fields if provided
                if (update.containsKey("price")) {
                    product.setPrice(new BigDecimal(update.get("price").toString()));
                }
                if (update.containsKey("stockQuantity")) {
                    product.setStockQuantity(Integer.valueOf(update.get("stockQuantity").toString()));
                }
                if (update.containsKey("lowStockThreshold")) {
                    product.setLowStockThreshold(Integer.valueOf(update.get("lowStockThreshold").toString()));
                }

                productRepository.save(product);
                successCount++;
            } catch (Exception e) {
                errors.add(String.format("Row %d: %s", rowNumber, e.getMessage()));
            }
        }

        auditService.log(
            AuditLog.ActionType.BULK_UPDATE,
            AuditLog.EntityType.PRODUCT,
            null,
            null,
            successCount + " products updated",
            "Bulk product update completed"
        );

        return Map.of(
            "success", successCount,
            "failed", errors.size(),
            "total", updates.size(),
            "errors", errors
        );
    }

    /**
     * Bulk delete products
     */
    @Transactional
    public Map<String, Object> bulkDeleteProducts(List<Long> productIds) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (Long productId : productIds) {
            try {
                Product product = productRepository.findByIdAndOrganizationId(productId, organizationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

                // Soft delete
                product.setIsActive(false);
                productRepository.save(product);
                successCount++;
            } catch (Exception e) {
                errors.add(String.format("Product %d: %s", productId, e.getMessage()));
            }
        }

        auditService.log(
            AuditLog.ActionType.BULK_DELETE,
            AuditLog.EntityType.PRODUCT,
            null,
            null,
            successCount + " products deleted",
            "Bulk product deletion completed"
        );

        return Map.of(
            "success", successCount,
            "failed", errors.size(),
            "total", productIds.size(),
            "errors", errors
        );
    }

    /**
     * Bulk update stock quantities
     */
    @Transactional
    public Map<String, Object> bulkUpdateStock(List<Map<String, Object>> stockUpdates) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (Map<String, Object> update : stockUpdates) {
            try {
                Long productId = Long.valueOf(update.get("productId").toString());
                int quantityChange = Integer.valueOf(update.get("quantityChange").toString());

                Product product = productRepository.findByIdAndOrganizationId(productId, organizationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

                int newQuantity = product.getStockQuantity() + quantityChange;
                if (newQuantity < 0) {
                    errors.add(String.format("Product %d: Insufficient stock", productId));
                    continue;
                }

                product.setStockQuantity(newQuantity);
                productRepository.save(product);
                successCount++;
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }

        return Map.of(
            "success", successCount,
            "failed", errors.size(),
            "total", stockUpdates.size(),
            "errors", errors
        );
    }
}
