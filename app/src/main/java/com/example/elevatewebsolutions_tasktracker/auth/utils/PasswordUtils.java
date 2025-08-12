package com.example.elevatewebsolutions_tasktracker.auth.utils;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// ref https://github.com/benjaminsaff/password-utils-java
// ref https://www.geeksforgeeks.org/android/how-to-validate-password-from-text-input-in-android/

/**
 * Utility class for secure password hashing and verification
 * Uses SHA-256 with salt for password security
 */
public class PasswordUtils {
    private static final String TAG = "PasswordUtils";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32; // 32 bytes = 256 bits
    private static final int HASH_ITERATIONS = 10000; // Number of iterations for additional security

    /**
     * Generates a cryptographically secure random salt
     * @return Base64 encoded salt string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with the provided salt using SHA-256
     * @param password The plain text password
     * @param salt The salt to use for hashing
     * @return Base64 encoded hash string
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

            // Combine password and salt
            String saltedPassword = password + salt;
            byte[] passwordBytes = saltedPassword.getBytes(StandardCharsets.UTF_8);

            // Hash multiple times for additional security
            byte[] hash = passwordBytes;
            for (int i = 0; i < HASH_ITERATIONS; i++) {
                digest.reset();
                hash = digest.digest(hash);
            }

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Hash algorithm not available: " + HASH_ALGORITHM, e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Convenience method to hash a password with a new salt
     * @param password The plain text password
     * @return Array containing [hashedPassword, salt]
     */
    public static String[] hashPasswordWithNewSalt(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return new String[]{hashedPassword, salt};
    }

    /**
     * Verifies a password against a stored hash and salt
     * @param password The plain text password to verify
     * @param storedHash The stored hash to compare against
     * @param salt The salt that was used to create the stored hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        if (password == null || storedHash == null || salt == null) {
            return false;
        }

        try {
            String hashedInput = hashPassword(password, salt);
            return constantTimeEquals(hashedInput, storedHash);
        } catch (Exception e) {
            Log.e(TAG, "Error verifying password", e);
            return false;
        }
    }

    /**
     * Constant time string comparison to prevent timing attacks
     * @param a First string
     * @param b Second string
     * @return true if strings are equal, false otherwise
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Add more complex validation rules here if needed
        // For now, just check minimum length and not empty
        return !password.trim().isEmpty();
    }

    /**
     * Gets password strength score (0-100)
     * @param password The password to score
     * @return Score from 0 (weak) to 100 (strong)
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Length scoring
        if (password.length() >= 8) score += 25;
        if (password.length() >= 12) score += 25;

        // Character variety scoring
        if (password.matches(".*[a-z].*")) score += 10; // lowercase
        if (password.matches(".*[A-Z].*")) score += 10; // uppercase
        if (password.matches(".*[0-9].*")) score += 15; // numbers
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score += 15; // special chars

        return Math.min(100, score);
    }

    /**
     * Validates username format
     * @param username The username to validate
     * @return true if username is valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Username should be between 3-50 characters and contain only alphanumeric characters and underscores
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }
}
