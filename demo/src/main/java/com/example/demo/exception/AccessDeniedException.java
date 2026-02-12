package com.example.demo.exception;

/**
 * Custom exception for access denied errors
 * Returns HTTP 403 status
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
