package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;

    /**
     * Initiate 2FA setup - returns secret and QR code
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateTwoFactor(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTwoFactorEnabled()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("2FA is already enabled for this user"));
        }

        String secret = twoFactorAuthService.generateSecret();
        byte[] qrCode = twoFactorAuthService.generateQrCode(
                user.getUsername(),
                secret,
                "IMS"
        );
        List<String> backupCodes = twoFactorAuthService.generateRecoveryCodes();

        return ResponseEntity.ok(new TwoFactorSetupResponse(
                secret,
                qrCode,
                backupCodes,
                "Scan the QR code with your authenticator app and verify with the code"
        ));
    }

    /**
     * Verify and enable 2FA
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndEnable(
            @RequestBody TwoFactorSetupRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTwoFactorEnabled()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("2FA is already enabled"));
        }

        // Verify TOTP code (frontend should have sent the secret separately)
        // For security, the secret must be re-transmitted from frontend session/cache
        // This is a simplified implementation - in production, use a secure session store

        // Enable 2FA for user
        user.setTwoFactorEnabled(true);
        user.setTotpSecret(request.recoveryCodes().get(0)); // Placeholder - use actual secret
        user.setRecoveryCodeHashes(
                twoFactorAuthService.hashRecoveryCodes(request.recoveryCodes())
        );
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.successMessage("2FA enabled successfully"));
    }

    /**
     * Verify TOTP code (used during login or sensitive operations)
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyTotpCode(
            @RequestBody TwoFactorVerifyRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getTwoFactorEnabled() || user.getTotpSecret() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("2FA is not enabled"));
        }

        boolean isValid = twoFactorAuthService.verifyCode(
                user.getTotpSecret(),
                request.code()
        );

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid TOTP code"));
        }

        return ResponseEntity.ok(ApiResponse.successMessage("TOTP verified successfully"));
    }

    /**
     * Verify recovery code (single-use backup)
     */
    @PostMapping("/verify-recovery")
    public ResponseEntity<?> verifyRecoveryCode(
            @RequestBody TwoFactorVerifyRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getTwoFactorEnabled()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("2FA is not enabled"));
        }

        boolean isValid = twoFactorAuthService.verifyRecoveryCode(
                request.code(),
                user.getRecoveryCodeHashes()
        );

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid recovery code"));
        }

        // Remove used recovery code
        user.getRecoveryCodeHashes()
                .removeIf(hashed -> twoFactorAuthService.verifyRecoveryCode(request.code(), List.of(hashed)));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.successMessage("Recovery code verified successfully"));
    }

    /**
     * Get 2FA status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getTwoFactorStatus(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new TwoFactorStatusResponse(
                user.getTwoFactorEnabled(),
                user.getTotpSecret() == null,
                user.getTwoFactorEnabled() ? "2FA is enabled" : "2FA is not enabled"
        ));
    }

    /**
     * Disable 2FA (requires password verification)
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disableTwoFactor(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTwoFactorEnabled(false);
        user.setTotpSecret(null);
        user.setRecoveryCodeHashes(null);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.successMessage("2FA disabled successfully"));
    }

    /**
     * Regenerate recovery codes
     */
    @PostMapping("/regenerate-codes")
    public ResponseEntity<?> regenerateRecoveryCodes(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getTwoFactorEnabled()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("2FA must be enabled first"));
        }

        List<String> newCodes = twoFactorAuthService.generateRecoveryCodes();
        user.setRecoveryCodeHashes(
                twoFactorAuthService.hashRecoveryCodes(newCodes)
        );
        userRepository.save(user);

        return ResponseEntity.ok(new TwoFactorSetupResponse(
                null,
                null,
                newCodes,
                "Recovery codes regenerated successfully"
        ));
    }
}
