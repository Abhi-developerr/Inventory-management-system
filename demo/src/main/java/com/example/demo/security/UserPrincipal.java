package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security
 * Contains user authentication info + organization context
 * 
 * Why create a custom UserPrincipal?
 * - Include organization_id in security context (critical for multi-tenancy)
 * - Add custom fields beyond username/password
 * - Decouple security layer from entity layer
 * - Control what information is available in SecurityContext
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String email;
    private final String password;
    private final Role role;
    private final Long organizationId;
    private final String organizationName;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method to create UserPrincipal from User entity
     */
    public static UserPrincipal create(User user) {
        // Convert role to Spring Security authority
        // Format: ROLE_ADMIN, ROLE_STAFF, etc.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            user.getOrganization().getId(),
            user.getOrganization().getName(),
            user.getIsActive() && user.getOrganization().getIsActive(),
            Collections.singletonList(authority)
        );
    }

    /**
     * Constructor
     */
    public UserPrincipal(Long userId, String username, String email, String password,
                        Role role, Long organizationId, String organizationName,
                        boolean isActive, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    // Spring Security UserDetails interface methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    /**
     * Helper method to check if user has specific role
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    /**
     * Helper method to check if user is admin or above
     */
    public boolean isAdminOrAbove() {
        return this.role == Role.ADMIN || this.role == Role.SUPER_ADMIN;
    }
}
