package com.example.demo.service;

import com.example.demo.dto.SupplierRequest;
import com.example.demo.dto.SupplierResponse;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.entity.SupplierProduct;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierProductRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierService.class);

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ProductRepository productRepository;

    public SupplierResponse createSupplier(SupplierRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        if (supplierRepository.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new BadRequestException("Supplier with code '" + request.getCode() + "' already exists");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .contactPerson(request.getContactPerson())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .leadTimeDays(request.getLeadTimeDays())
                .minimumOrderQuantity(request.getMinimumOrderQuantity())
                .paymentTerms(request.getPaymentTerms())
                .rating(request.getRating())
                .reliabilityScore(request.getReliabilityScore())
                .isActive(true)
                .organization(organization)
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToResponse(savedSupplier);
    }

    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Supplier supplier = supplierRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return mapToResponse(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Supplier> suppliers = supplierRepository.findByOrganizationIdAndIsActiveTrue(organizationId, pageable);
        return suppliers.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> searchSuppliers(String search, Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Supplier> suppliers = supplierRepository.searchSuppliers(organizationId, search, pageable);
        return suppliers.map(this::mapToResponse);
    }

    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Supplier supplier = supplierRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        if (!supplier.getCode().equals(request.getCode()) &&
            supplierRepository.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new BadRequestException("Supplier with code '" + request.getCode() + "' already exists");
        }

        supplier.setName(request.getName());
        supplier.setCode(request.getCode());
        supplier.setDescription(request.getDescription());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setContactPhone(request.getContactPhone());
        supplier.setAddress(request.getAddress());
        supplier.setLeadTimeDays(request.getLeadTimeDays());
        supplier.setMinimumOrderQuantity(request.getMinimumOrderQuantity());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setRating(request.getRating());
        supplier.setReliabilityScore(request.getReliabilityScore());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToResponse(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Supplier supplier = supplierRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    public void linkProductToSupplier(Long supplierId, Long productId, BigDecimal costPrice, String supplierSku) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Supplier supplier = supplierRepository.findByIdAndOrganizationId(supplierId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        Product product = productRepository.findByIdAndOrganizationId(productId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (supplierProductRepository.existsBySupplierIdAndProductId(supplierId, productId)) {
            throw new BadRequestException("Product is already linked to this supplier");
        }

        SupplierProduct supplierProduct = SupplierProduct.builder()
                .supplier(supplier)
                .product(product)
                .costPrice(costPrice)
                .supplierSku(supplierSku)
                .isAvailable(true)
                .build();

        supplierProductRepository.save(supplierProduct);
    }

    public void unlinkProductFromSupplier(Long supplierId, Long productId) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        supplierRepository.findByIdAndOrganizationId(supplierId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        productRepository.findByIdAndOrganizationId(productId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        supplierProductRepository.deleteBySupplierIdAndProductId(supplierId, productId);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        List<SupplierResponse.SupplierProductResponse> products = supplier.getSuppliedProducts().stream()
                .map(sp -> SupplierResponse.SupplierProductResponse.builder()
                        .id(sp.getId())
                        .productId(sp.getProduct().getId())
                        .productName(sp.getProduct().getName())
                        .productSku(sp.getProduct().getSku())
                        .supplierSku(sp.getSupplierSku())
                        .costPrice(sp.getCostPrice())
                        .isAvailable(sp.getIsAvailable())
                        .notes(sp.getNotes())
                        .build())
                .collect(Collectors.toList());

        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .code(supplier.getCode())
                .description(supplier.getDescription())
                .contactPerson(supplier.getContactPerson())
                .contactEmail(supplier.getContactEmail())
                .contactPhone(supplier.getContactPhone())
                .address(supplier.getAddress())
                .leadTimeDays(supplier.getLeadTimeDays())
                .minimumOrderQuantity(supplier.getMinimumOrderQuantity())
                .paymentTerms(supplier.getPaymentTerms())
                .isActive(supplier.getIsActive())
                .rating(supplier.getRating())
                .reliabilityScore(supplier.getReliabilityScore())
                .organizationId(supplier.getOrganization().getId())
                .products(products)
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
