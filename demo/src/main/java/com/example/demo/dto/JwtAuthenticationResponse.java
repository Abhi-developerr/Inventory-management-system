package com.example.demo.dto;

import com.example.demo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT authentication response DTO
 * Returned after successful login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Long organizationId;
    private String organizationName;
    private Long expiresIn; // milliseconds

    public JwtAuthenticationResponse(String accessToken, Long userId, String username, 
                                    String email, Role role, Long organizationId, 
                                    String organizationName, Long expiresIn) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.expiresIn = expiresIn;
    }
}
