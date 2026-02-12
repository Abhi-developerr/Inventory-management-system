package com.example.demo.exception;

/**
 * Custom exception for bad request errors
 * Returns HTTP 400 status
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
