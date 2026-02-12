package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Auditing Configuration
 * Automatically populates createdAt, updatedAt, createdBy, updatedBy fields
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
}

/**
 * Provides current user ID for audit fields
 * Extracts user ID from Spring Security context
 */
@Component("auditorProvider")
class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
            || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        // Extract user ID from authenticated principal
        try {
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.example.demo.security.UserPrincipal) {
                com.example.demo.security.UserPrincipal userPrincipal = 
                    (com.example.demo.security.UserPrincipal) principal;
                return Optional.of(userPrincipal.getUserId());
            }
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.empty();
    }
}
