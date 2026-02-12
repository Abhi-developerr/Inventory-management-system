package com.example.demo.event;

import com.example.demo.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderEvent {
    private String eventId;
    private String eventType; // CREATED, STATUS_UPDATED, CANCELLED
    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private Long organizationId;
    private Long userId;
    private Double totalAmount;
    private LocalDateTime occurredAt;
    private List<OrderItemEvent> items;

    @Data
    @Builder
    public static class OrderItemEvent {
        private Long productId;
        private Integer quantity;
        private Double unitPrice;
    }
}
