package com.example.elevatewebsolutions_tasktracker.auth.models;

import com.example.elevatewebsolutions_tasktracker.database.entities.User;

/**
 * Result class for authentication operations
 * Contains success/failure status, user data, and error information
 */
public class AuthenticationResult {
    private final boolean success;
    private final User user;
    private final String errorMessage;
    private final AuthError errorType;

    public enum AuthError {
        INVALID_CREDENTIALS,
        USER_NOT_FOUND,
        ACCOUNT_LOCKED,
        INVALID_INPUT,
        NETWORK_ERROR,
        UNKNOWN_ERROR
    }

    // Success constructor
    public AuthenticationResult(User user) {
        this.success = true;
        this.user = user;
        this.errorMessage = null;
        this.errorType = null;
    }

    // Failure constructor
    public AuthenticationResult(AuthError errorType, String errorMessage) {
        this.success = false;
        this.user = null;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public AuthError getErrorType() {
        return errorType;
    }

    public boolean isAdmin() {
        return success && user != null && user.getAdmin();
    }
}
