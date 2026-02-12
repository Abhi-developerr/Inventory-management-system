package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Organization;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * Handles user registration and related operations
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register new user
     * 
     * @param request registration details
     * @return created user
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Verify organization exists and is active
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!organization.getIsActive()) {
            throw new BadRequestException("Organization is not active");
        }

        // Check organization user limit
        long currentUserCount = userRepository.countByOrganizationIdAndIsActiveTrue(organization.getId());
        if (organization.getMaxUsers() != null && currentUserCount >= organization.getMaxUsers()) {
            throw new BadRequestException("Organization has reached maximum user limit");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .organization(organization)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }
}
