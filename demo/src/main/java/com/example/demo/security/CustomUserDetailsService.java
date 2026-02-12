package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService for Spring Security
 * Loads user from database for authentication
 * 
 * Why implement UserDetailsService?
 * - Spring Security requires this to load user during authentication
 * - We load from our custom User entity, not default User table
 * - Includes organization context for multi-tenancy
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by username for Spring Security authentication
     * Called during login
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user with organization (EAGER fetch in entity)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        // Check if user and organization are active
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        if (!user.getOrganization().getIsActive()) {
            throw new UsernameNotFoundException("Organization is deactivated for user: " + username);
        }

        return UserPrincipal.create(user);
    }

    /**
     * Load user by ID (used in JWT filter)
     * Called for every authenticated request
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id: " + id)
                );

        // Validate user and organization status
        if (!user.getIsActive() || !user.getOrganization().getIsActive()) {
            throw new UsernameNotFoundException("User or organization is deactivated: " + id);
        }

        return UserPrincipal.create(user);
    }

    /**
     * Load user by email (for alternative login)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        if (!user.getIsActive() || !user.getOrganization().getIsActive()) {
            throw new UsernameNotFoundException("User or organization is deactivated: " + email);
        }

        return UserPrincipal.create(user);
    }
}
