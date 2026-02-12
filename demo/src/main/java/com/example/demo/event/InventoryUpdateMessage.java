package com.example.demo.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryUpdateMessage {
    private Long productId;
    private String productName;
    private String sku;
    private Double price;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean lowStock;
    private Long organizationId;
    private Long orderId;
    private String eventType;
    private LocalDateTime occurredAt;
}
