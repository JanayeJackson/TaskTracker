package com.example.elevatewebsolutions_tasktracker.auth.models;

import com.example.elevatewebsolutions_tasktracker.database.entities.User;

/**
 * User session class for managing active user sessions
 * Tracks login time, expiration, and user information
 */
public class UserSession {
    private final int userId;
    private final String username;
    private final boolean isAdmin;
    private final long loginTimestamp;
    private final long expirationTimestamp;

    // Session timeout: 24 hours (in milliseconds)
    private static final long SESSION_TIMEOUT = 24 * 60 * 60 * 1000;

    public UserSession(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.isAdmin = user.getAdmin();
        this.loginTimestamp = System.currentTimeMillis();
        this.expirationTimestamp = loginTimestamp + SESSION_TIMEOUT;
    }

    // Constructor for restoring session from saved data
    public UserSession(int userId, String username, boolean isAdmin, long loginTimestamp, long expirationTimestamp) {
        this.userId = userId;
        this.username = username;
        this.isAdmin = isAdmin;
        this.loginTimestamp = loginTimestamp;
        this.expirationTimestamp = expirationTimestamp;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public long getLoginTimestamp() {
        return loginTimestamp;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public boolean isValid() {
        return System.currentTimeMillis() < expirationTimestamp;
    }

    public boolean isExpired() {
        return !isValid();
    }

    public long getTimeRemaining() {
        return Math.max(0, expirationTimestamp - System.currentTimeMillis());
    }
}
