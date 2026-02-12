package com.example.demo.util;

import com.example.demo.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security utility class for accessing current user information
 * Provides convenient methods to extract user details from SecurityContext
 */
public class SecurityUtils {

    /**
     * Get current authenticated user
     * 
     * @return UserPrincipal of current user
     * @throws IllegalStateException if no user is authenticated
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        
        throw new IllegalStateException("Invalid user principal type");
    }

    /**
     * Get current user's organization ID
     * Critical for multi-tenancy - ensures data isolation
     * 
     * @return organization ID of current user
     */
    public static Long getCurrentUserOrganizationId() {
        return getCurrentUser().getOrganizationId();
    }

    /**
     * Get current user's ID
     * 
     * @return user ID of current user
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**     * Get current user's username
     * 
     * @return username of current user
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**     * Check if current user has specific role
     * 
     * @param roleName role name to check
     * @return true if user has the role
     */
    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }

    /**
     * Check if current user is SUPER_ADMIN
     */
    public static boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * Check if current user is ADMIN or above
     */
    public static boolean isAdminOrAbove() {
        return hasRole("ADMIN") || hasRole("SUPER_ADMIN");
    }
}
