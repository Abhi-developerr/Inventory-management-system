package com.example.demo.dto;

import java.util.List;

public record TwoFactorSetupResponse(
    String secret,
    byte[] qrCode,
    List<String> backupCodes,
    String message
) {}
