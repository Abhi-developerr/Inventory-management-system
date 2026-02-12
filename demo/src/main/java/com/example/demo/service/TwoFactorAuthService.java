package com.example.demo.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TwoFactorAuthService {
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final PasswordEncoder passwordEncoder;

    public TwoFactorAuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate a new TOTP secret for user enrollment
     */
    public String generateSecret() {
        return secretGenerator.generate();
    }

    /**
     * Verify a TOTP code against the secret
     */
    public boolean verifyCode(String secret, String code) {
        try {
            return codeVerifier.isValidCode(secret, code);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate QR code for TOTP setup
     */
    public byte[] generateQrCode(String username, String secret, String issuer) {
        try {
            QrData data = new QrDataFactory(HashingAlgorithm.SHA1, 6, 30)
                    .newBuilder()
                    .label(username)
                    .secret(secret)
                    .issuer(issuer)
                    .build();

            return qrGenerator.generate(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Generate recovery codes (8 codes, 8 characters each)
     */
    public List<String> generateRecoveryCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            codes.add(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return codes;
    }

    /**
     * Hash recovery codes for storage in database
     */
    public List<String> hashRecoveryCodes(List<String> codes) {
        return codes.stream()
                .map(passwordEncoder::encode)
                .toList();
    }

    /**
     * Verify recovery code
     */
    public boolean verifyRecoveryCode(String code, List<String> hashedCodes) {
        return hashedCodes.stream()
                .anyMatch(hashed -> passwordEncoder.matches(code, hashed));
    }

    /**
     * Generate current TOTP code (for debugging/testing)
     */
    public String generateCurrentCode(String secret) {
        try {
            long currentBucket = Math.floorDiv(timeProvider.getTime(), 30);
            return codeGenerator.generate(secret, currentBucket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code", e);
        }
    }
}
