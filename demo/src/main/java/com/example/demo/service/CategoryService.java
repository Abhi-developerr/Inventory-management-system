package com.example.demo.service;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Organization;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Category Service - Business logic for category management
 * 
 * Key responsibilities:
 * - Enforce multi-tenancy (organization filtering)
 * - Validate business rules
 * - Transform entities to DTOs
 * - Handle transactions
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Create new category
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        // Check if category name already exists in this organization
        if (categoryRepository.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .organization(organization)
                .isActive(true)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    /**
     * Get all categories for current organization
     */
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Category> categories = categoryRepository.findByOrganizationId(organizationId, pageable);
        return categories.map(this::mapToResponse);
    }

    /**
     * Get active categories (for dropdown lists)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        List<Category> categories = categoryRepository.findByOrganizationIdAndIsActiveTrue(organizationId);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Category category = categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToResponse(category);
    }

    /**
     * Update category
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Category category = categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if new name conflicts with existing category
        if (!category.getName().equals(request.getName()) &&
            categoryRepository.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    /**
     * Delete category (soft delete)
     */
    public void deleteCategory(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Category category = categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Soft delete
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    /**
     * Map entity to response DTO
     */
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .organizationId(category.getOrganization().getId())
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
