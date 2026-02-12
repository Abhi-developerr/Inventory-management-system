package com.example.demo.enums;

/**
 * Organization subscription plan types
 * Different plans can have different feature limits
 */
public enum PlanType {
    FREE,       // Limited features, max 100 products
    BASIC,      // Standard features, max 1000 products
    PREMIUM,    // All features, unlimited products
    ENTERPRISE  // Custom features and dedicated support
}
