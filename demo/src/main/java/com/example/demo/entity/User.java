package com.example.demo.entity;

import com.example.demo.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users", 
    indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_org", columnList = "organization_id"),
        @Index(name = "idx_user_org_username", columnList = "organization_id, username")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_email", columnNames = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // 2FA TOTP Support
    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(name = "totp_secret", length = 255)
    private String totpSecret;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_recovery_codes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "hashed_code")
    private List<String> recoveryCodeHashes;

    // Many users belong to one organization
    // FetchType.EAGER: We always need organization info during authentication
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_user_organization"))
    private Organization organization;

    public String getFullName() {
        return firstName != null && lastName != null 
            ? firstName + " " + lastName 
            : username;
    }
}
