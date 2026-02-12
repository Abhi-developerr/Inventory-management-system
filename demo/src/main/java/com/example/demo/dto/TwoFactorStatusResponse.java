package com.example.demo.dto;

public record TwoFactorStatusResponse(
    boolean enabled,
    boolean setupRequired,
    String message
) {}
