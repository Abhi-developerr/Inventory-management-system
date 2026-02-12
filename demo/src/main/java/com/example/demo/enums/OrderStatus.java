package com.example.demo.enums;

/**
 * Order lifecycle status
 */
public enum OrderStatus {
    CREATED,    // Order created, payment pending
    CONFIRMED,  // Payment confirmed, ready to ship
    SHIPPED,    // Order dispatched
    DELIVERED,  // Order delivered to customer
    CANCELLED   // Order cancelled (stock should be restored)
}
