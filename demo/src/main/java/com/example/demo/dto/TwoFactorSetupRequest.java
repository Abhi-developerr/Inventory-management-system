package com.example.demo.dto;

import java.util.List;

public record TwoFactorSetupRequest(
    String totpCode,
    List<String> recoveryCodes
) {}
