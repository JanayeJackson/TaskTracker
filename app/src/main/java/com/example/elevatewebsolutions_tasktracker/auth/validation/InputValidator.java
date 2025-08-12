package com.example.elevatewebsolutions_tasktracker.auth.validation;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Input validation utility for authentication
 * Provides basic validation for usernames, passwords, and other authentication fields
 */
public class InputValidator {

    // Validation constants
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 128;

    // Regex patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    /**
     * basic validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }
    }

    /**
     * Validates username according to application rules
     * @param username Username to validate
     * @return ValidationResult with details
     */
    public static ValidationResult validateUsername(String username) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("Username cannot be empty");
            return new ValidationResult(false, errors);
        }

        String trimmedUsername = username.trim();

        if (trimmedUsername.length() < MIN_USERNAME_LENGTH) {
            errors.add("Username must be at least " + MIN_USERNAME_LENGTH + " characters long");
        }

        if (trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            errors.add("Username cannot exceed " + MAX_USERNAME_LENGTH + " characters");
        }

        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            errors.add("Username can only contain letters, numbers, and underscores");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates password according to basic security requirements
     * @param password Password to validate
     * @return ValidationResult with details
     */
    public static ValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password cannot be empty");
            return new ValidationResult(false, errors);
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates login request fields
     * @param username Username to validate
     * @param password Password to validate
     * @return ValidationResult for the complete login request
     */
    public static ValidationResult validateLoginRequest(String username, String password) {
        List<String> allErrors = new ArrayList<>();

        ValidationResult usernameResult = validateUsername(username);
        ValidationResult passwordResult = validatePassword(password);

        allErrors.addAll(usernameResult.getErrors());
        allErrors.addAll(passwordResult.getErrors());

        return new ValidationResult(allErrors.isEmpty(), allErrors);
    }

    /**
     * Validates email format (if email login is supported)
     * @param email Email to validate
     * @return ValidationResult for email
     */
    public static ValidationResult validateEmail(String email) {
        List<String> errors = new ArrayList<>();

        if (email == null || email.trim().isEmpty()) {
            errors.add("Email cannot be empty");
            return new ValidationResult(false, errors);
        }

        String trimmedEmail = email.trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            errors.add("Invalid email format");
        }

        if (trimmedEmail.length() > 254) { // RFC 5321 limit
            errors.add("Email address is too long");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Sanitizes input string by removing potentially harmful characters
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove null bytes and control characters except tabs, newlines, and carriage returns
        return input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "").trim();
    }
}
