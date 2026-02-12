package com.example.demo.exception;

/**
 * Custom exception for insufficient stock errors
 * Returns HTTP 400 status
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productName, Integer available, Integer required) {
        super(String.format("Insufficient stock for product '%s'. Available: %d, Required: %d", 
                          productName, available, required));
    }
}
