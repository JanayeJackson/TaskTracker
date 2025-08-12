package com.example.elevatewebsolutions_tasktracker.auth.models;

/**
 * Enum for user roles in the system
 * Provides type-safe role management
 */
public enum UserRole {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public static UserRole fromBoolean(boolean isAdmin) {
        return isAdmin ? ADMIN : USER;
    }
}
