package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.event.OrderEvent;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service - Business logic for order management
 * 
 * Critical features:
 * - Transactional order creation with stock deduction
 * - Optimistic locking for concurrent stock updates
 * - Order status lifecycle management
 * - Stock restoration on cancellation
 * 
 * Transaction strategy:
 * - REQUIRED: Order creation modifies multiple entities atomically
 * - SERIALIZABLE isolation for stock updates (prevents race conditions)
 */
@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topics.order-events}")
    private String orderEventsTopic;

    /**
     * Create new order
     * 
     * CRITICAL: This method MUST be transactional
     * - Order creation and stock deduction happen atomically
     * - If any step fails, entire transaction rolls back
     * - Prevents overselling and data inconsistency
     * 
     * Uses SERIALIZABLE isolation to prevent concurrent stock issues
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderResponse createOrder(OrderRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Long userId = SecurityUtils.getCurrentUserId();

        // Load organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        // Load user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate and prepare order items
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Load product (ensure it belongs to same organization)
            Product product = productRepository.findByIdAndOrganizationId(
                    itemRequest.getProductId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", itemRequest.getProductId()));

            // Check if product is active
            if (!product.getIsActive()) {
                throw new BadRequestException("Product '" + product.getName() + "' is not available");
            }

            // Check stock availability
            if (!product.hasStock(itemRequest.getQuantity())) {
                throw new InsufficientStockException(
                        product.getName(), 
                        product.getStockQuantity(), 
                        itemRequest.getQuantity()
                );
            }

            // Reduce stock (optimistic locking prevents race conditions)
            product.reduceStock(itemRequest.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice()) // Capture current price
                    .build();
            
            orderItems.add(orderItem);
        }

        // Generate unique order number
        String orderNumber = generateOrderNumber();

        // Create order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .status(OrderStatus.CREATED)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .organization(organization)
                .user(user)
                .orderDate(LocalDateTime.now())
                .build();

        // Add items to order (bidirectional relationship)
        for (OrderItem item : orderItems) {
            order.addOrderItem(item);
        }

        // Calculate total amount
        order.calculateTotalAmount();

        // Save order (cascades to order items)
        Order savedOrder = orderRepository.save(order);

        publishOrderEvent(savedOrder, "CREATED");

        return mapToResponse(savedOrder);
    }

    /**
     * Get all orders for current organization
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Order> orders = orderRepository.findByOrganizationId(organizationId, pageable);
        return orders.map(this::mapToResponse);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Order order = orderRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapToResponse(order);
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Order> orders = orderRepository.findByOrganizationIdAndStatus(
                organizationId, status, pageable);
        return orders.map(this::mapToResponse);
    }

    /**
     * Search orders
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(String search, Pageable pageable) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();
        Page<Order> orders = orderRepository.searchOrders(organizationId, search, pageable);
        return orders.map(this::mapToResponse);
    }

    /**
     * Update order status
     * 
     * Status transitions:
     * CREATED → CONFIRMED → SHIPPED → DELIVERED
     * CREATED/CONFIRMED → CANCELLED (restores stock)
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Order order = orderRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        // If cancelling order, restore stock
        if (newStatus == OrderStatus.CANCELLED && 
            (oldStatus == OrderStatus.CREATED || oldStatus == OrderStatus.CONFIRMED)) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        publishOrderEvent(updatedOrder, "STATUS_UPDATED");

        return mapToResponse(updatedOrder);
    }

    /**
     * Cancel order
     * Restores stock for all items
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderResponse cancelOrder(Long id) {
        Long organizationId = SecurityUtils.getCurrentUserOrganizationId();

        Order order = orderRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Can only cancel if not already shipped/delivered
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order that has been shipped or delivered");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        // Restore stock
        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        publishOrderEvent(cancelledOrder, "CANCELLED");

        return mapToResponse(cancelledOrder);
    }

    /**
     * Restore stock for cancelled order
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.restoreStock(item.getQuantity());
            productRepository.save(product);
        }
    }

    /**
     * Validate order status transition
     */
    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        // Define valid transitions
        switch (from) {
            case CREATED:
                if (to != OrderStatus.CONFIRMED && to != OrderStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Invalid status transition from CREATED to " + to);
                }
                break;
            case CONFIRMED:
                if (to != OrderStatus.SHIPPED && to != OrderStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Invalid status transition from CONFIRMED to " + to);
                }
                break;
            case SHIPPED:
                if (to != OrderStatus.DELIVERED) {
                    throw new BadRequestException(
                            "Invalid status transition from SHIPPED to " + to);
                }
                break;
            case DELIVERED:
                throw new BadRequestException("Cannot change status of delivered order");
            case CANCELLED:
                throw new BadRequestException("Cannot change status of cancelled order");
        }
    }

    /**
     * Generate unique order number
     * Format: ORD-{timestamp}-{random}
     */
    private String generateOrderNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    /**
     * Publish order lifecycle events to Kafka for downstream consumers
     */
    private void publishOrderEvent(Order order, String eventType) {
        try {
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .status(order.getStatus())
                    .organizationId(order.getOrganization().getId())
                    .userId(order.getUser() != null ? order.getUser().getId() : null)
                    .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0d)
                    .occurredAt(LocalDateTime.now())
                    .items(order.getOrderItems().stream()
                            .map(item -> OrderEvent.OrderItemEvent.builder()
                                    .productId(item.getProduct().getId())
                                    .quantity(item.getQuantity())
                                    .unitPrice(item.getPrice() != null ? item.getPrice().doubleValue() : 0d)
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            kafkaTemplate.send(orderEventsTopic, order.getOrderNumber(), event);
            log.info("Published order event: type={} order={} topic={}", eventType, order.getId(), orderEventsTopic);
        } catch (Exception ex) {
            log.error("Failed to publish order event for order {}", order.getId(), ex);
        }
    }

    /**
     * Map entity to response DTO
     */
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::mapOrderItemToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                .organizationId(order.getOrganization().getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getUser() != null ? order.getUser().getFullName() : null)
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Map order item to response DTO
     */
    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productSku(item.getProduct().getSku())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
