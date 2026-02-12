package com.example.demo.enums;

public enum ReportType {
    SALES_SUMMARY,           // Revenue, orders, top products
    INVENTORY_STATUS,        // Stock levels, low stock, overstocked
    LOW_STOCK_ALERT,        // Products below threshold
    ORDER_ANALYSIS,         // Orders by status, time period
    SUPPLIER_PERFORMANCE,   // Supplier reliability, delivery times
    REVENUE_TREND,          // Revenue over time
    STOCK_MOVEMENT,         // Inventory in/out
    CUSTOMER_ORDERS,        // Orders by customer/organization
    PRODUCT_PERFORMANCE,    // Best/worst selling products
    EXPIRING_INVENTORY      // Stock expiration warnings
}
