package com.example.demo.enums;

/**
 * User roles in the system with hierarchical permissions
 * SUPER_ADMIN: Platform administrator (manages all organizations)
 * ADMIN: Organization administrator (full access within their org)
 * STAFF: Regular employee (limited access within their org)
 */
public enum Role {
    SUPER_ADMIN,  // Can manage all organizations and users
    ADMIN,        // Can manage their organization's data and users
    STAFF         // Can view and create orders, limited product management
}
