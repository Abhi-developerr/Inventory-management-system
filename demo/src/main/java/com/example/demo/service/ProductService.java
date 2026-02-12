package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Product;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Product Service - Business logic for product/inventory management
 * 
 * Key features:
 * - Multi-tenant data isolation
 * - Stock management
 * - Low stock alerts
 * - Search and filtering
 */
@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ProductSearchService productSearchService;

    /**
     * Create new product
     */
    public ProductResponse createProduct(ProductRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        // Check SKU uniqueness within organization
        if (productRepository.existsBySkuAndOrganizationId(request.getSku(), organizationId)) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        // Validate category belongs to same organization
        Category category = categoryRepository.findByIdAndOrganizationId(request.getCategoryId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        // Check product limit for organization
        long productCount = productRepository.countByOrganizationIdAndIsActiveTrue(organizationId);
        if (organization.getMaxProducts() != null && productCount >= organization.getMaxProducts()) {
            throw new BadRequestException("Organization has reached maximum product limit");
        }

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .imageUrl(request.getImageUrl())
                .category(category)
                .organization(organization)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        productSearchService.index(savedProduct);
        return mapToResponse(savedProduct);
    }

    /**
     * Get all products with pagination
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Product> products = productRepository.findByOrganizationId(organizationId, pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponse(product);
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        
        // Verify category belongs to organization
        categoryRepository.findByIdAndOrganizationId(categoryId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<Product> products = productRepository.findByOrganizationIdAndCategoryId(
                organizationId, categoryId, pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Search products by name, SKU, or description
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        if (productSearchService.isEnabled()) {
            try {
                Page<ProductResponse> results = productSearchService.search(search, organizationId, pageable);
                if (results.hasContent()) {
                    return results;
                }
            } catch (Exception ex) {
                log.warn("Elasticsearch search failed, falling back to DB", ex);
            }
        }

        Page<Product> products = productRepository.searchProducts(organizationId, search, pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get product by barcode (for barcode scanning)
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductByBarcode(String barcodeNumber) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Product product = productRepository.findByBarcodeNumberAndOrganizationId(barcodeNumber, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcodeNumber));
        return mapToResponse(product);
    }

    /**
     * Get low stock products
     * Critical for inventory management - alerts when restocking needed
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        List<Product> products = productRepository.findLowStockProducts(organizationId);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update product
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Check SKU uniqueness if changed
        if (!product.getSku().equals(request.getSku()) &&
            productRepository.existsBySkuAndOrganizationId(request.getSku(), organizationId)) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        // Validate category belongs to same organization
        Category category = categoryRepository.findByIdAndOrganizationId(request.getCategoryId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        productSearchService.index(updatedProduct);
        return mapToResponse(updatedProduct);
    }

    /**
     * Update stock quantity
     * Separate method for stock adjustments (inventory corrections)
     */
    public ProductResponse updateStock(Long id, Integer quantity) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (quantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        product.setStockQuantity(quantity);
        Product updatedProduct = productRepository.save(product);
        productSearchService.index(updatedProduct);
        return mapToResponse(updatedProduct);
    }

    /**
     * Delete product (soft delete)
     */
    public void deleteProduct(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setIsActive(false);
        productRepository.save(product);
        productSearchService.delete(product.getId());
    }

    /**
     * Map entity to response DTO
     * Excludes sensitive information and includes computed fields
     */
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .isActive(product.getIsActive())
                .isLowStock(product.isLowStock())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .organizationId(product.getOrganization().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
