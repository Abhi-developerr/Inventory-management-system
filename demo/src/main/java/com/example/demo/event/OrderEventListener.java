package com.example.demo.event;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.NotificationService;
import com.example.demo.event.InventoryUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final CacheManager cacheManager;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderEventListener(OrderRepository orderRepository,
                              ProductRepository productRepository,
                              NotificationService notificationService,
                              CacheManager cacheManager,
                              SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.cacheManager = cacheManager;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderEventsListenerContainerFactory")
    @Transactional
    public void onOrderEvent(OrderEvent event) {
        log.info("Received order event: type={} orderId={} status={} total={}",
                event.getEventType(), event.getOrderId(), event.getStatus(), event.getTotalAmount());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found for event " + event.getOrderId()));

        handleNotification(order, event);
        evictAnalyticsCaches(order.getOrganization().getId());
        broadcastInventoryUpdates(order, event);
    }

    private void handleNotification(Order order, OrderEvent event) {
        if (order.getUser() == null) {
            log.debug("Skipping notification for order {} because no user is associated", order.getId());
            return;
        }

        Notification.NotificationType type = mapNotificationType(event);
        if (type == null) {
            return;
        }

        String title = "Order " + order.getOrderNumber() + " " + event.getStatus();
        String message = buildMessage(event, order);

        notificationService.createNotification(
                order.getUser(),
                order.getOrganization(),
                type,
                title,
                message,
                Notification.Priority.MEDIUM,
                Notification.RelatedEntityType.ORDER,
                order.getId()
        );
    }

    private Notification.NotificationType mapNotificationType(OrderEvent event) {
        if (event.getEventType() == null) {
            return null;
        }

        if ("CREATED".equalsIgnoreCase(event.getEventType())) {
            return Notification.NotificationType.ORDER_CREATED;
        }

        if ("CANCELLED".equalsIgnoreCase(event.getEventType())) {
            return Notification.NotificationType.ORDER_UPDATED;
        }

        if ("STATUS_UPDATED".equalsIgnoreCase(event.getEventType())) {
            if (event.getStatus() == null) {
                return Notification.NotificationType.ORDER_UPDATED;
            }
            switch (event.getStatus()) {
                case SHIPPED:
                    return Notification.NotificationType.ORDER_SHIPPED;
                case DELIVERED:
                    return Notification.NotificationType.ORDER_DELIVERED;
                default:
                    return Notification.NotificationType.ORDER_UPDATED;
            }
        }

        return null;
    }

    private String buildMessage(OrderEvent event, Order order) {
        if (event.getEventType() == null) {
            return "Order event received.";
        }

        switch (event.getEventType()) {
            case "CREATED":
                return "New order created with total " + order.getTotalAmount();
            case "CANCELLED":
                return "Order has been cancelled.";
            case "STATUS_UPDATED":
                return "Order status updated to " + event.getStatus();
            default:
                return "Order event received.";
        }
    }

    private void evictAnalyticsCaches(Long organizationId) {
        evict("dashboardAnalytics", organizationId);
        evict("salesTrend", organizationId);
    }

    private void evict(String cacheName, Long key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void broadcastInventoryUpdates(Order order, OrderEvent event) {
        if (event.getItems() == null || event.getItems().isEmpty()) {
            return;
        }

        Long orgId = order.getOrganization().getId();
        for (OrderEvent.OrderItemEvent item : event.getItems()) {
            if (item.getProductId() == null) {
                continue;
            }

            productRepository.findById(item.getProductId()).ifPresentOrElse(product -> {
                InventoryUpdateMessage payload = InventoryUpdateMessage.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .sku(product.getSku())
                        .price(product.getPrice() != null ? product.getPrice().doubleValue() : null)
                        .stockQuantity(product.getStockQuantity())
                        .lowStockThreshold(product.getLowStockThreshold())
                        .lowStock(product.isLowStock())
                        .organizationId(product.getOrganization().getId())
                        .orderId(order.getId())
                        .eventType(event.getEventType())
                        .occurredAt(event.getOccurredAt() != null ? event.getOccurredAt() : LocalDateTime.now())
                        .build();

                String destination = "/topic/inventory/" + orgId;
                messagingTemplate.convertAndSend(destination, payload);
            }, () -> log.warn("Product {} not found for inventory update", item.getProductId()));
        }
    }
}
