package com.example.demo.service;

import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.search.ProductDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.data.support.PageableExecutionUtils;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchService.class);

    private final ElasticsearchOperations operations;

    @Value("${app.search.elasticsearch.enabled:true}")
    private boolean enabled;

    public ProductSearchService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void index(Product product) {
        if (!enabled || product == null) {
            return;
        }
        try {
            ProductDocument doc = toDocument(product);
            operations.save(doc);
        } catch (Exception ex) {
            log.warn("Failed to index product {}", product.getId(), ex);
        }
    }

    public void delete(Long productId) {
        if (!enabled || productId == null) {
            return;
        }
        try {
            operations.delete(productId.toString(), IndexCoordinates.of("products"));
        } catch (Exception ex) {
            log.warn("Failed to delete product {} from index", productId, ex);
        }
    }

    public Page<ProductResponse> search(String text, Long organizationId, Pageable pageable) {
        if (!enabled) {
            return Page.empty(pageable);
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field("organizationId").value(organizationId)))
                        .must(m -> m.multiMatch(mm -> mm
                                .query(text)
                                .fields("name^3", "sku^5", "description^1.5", "categoryName^2")
                                .type(TextQueryType.BestFields)
                        ))
                ))
                .withPageable(pageable)
                .build();

        SearchHits<ProductDocument> hits = operations.search(query, ProductDocument.class);
        List<ProductResponse> results = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(results, pageable, hits::getTotalHits);
    }

    private ProductDocument toDocument(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(String.valueOf(product.getId()));
        doc.setProductId(product.getId());
        doc.setOrganizationId(product.getOrganization().getId());
        doc.setName(product.getName());
        doc.setSku(product.getSku());
        doc.setDescription(product.getDescription());
        doc.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        doc.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : null);
        doc.setStockQuantity(product.getStockQuantity());
        doc.setLowStockThreshold(product.getLowStockThreshold());
        doc.setIsActive(product.getIsActive());
        doc.setIsLowStock(product.isLowStock());
        doc.setCreatedAt(product.getCreatedAt());
        doc.setUpdatedAt(product.getUpdatedAt());
        return doc;
    }

    private ProductResponse toResponse(ProductDocument doc) {
        return ProductResponse.builder()
                .id(doc.getProductId())
                .name(doc.getName())
                .sku(doc.getSku())
                .description(doc.getDescription())
                .price(doc.getPrice() != null ? BigDecimal.valueOf(doc.getPrice()) : null)
                .stockQuantity(doc.getStockQuantity())
                .lowStockThreshold(doc.getLowStockThreshold())
                .isActive(doc.getIsActive())
                .isLowStock(Boolean.TRUE.equals(doc.getIsLowStock()))
                .imageUrl(null)
                .categoryId(null)
                .categoryName(doc.getCategoryName())
                .organizationId(doc.getOrganizationId())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
