package com.example.elevatewebsolutions_tasktracker.auth.services;

import android.content.Context;
import android.util.Log;

import com.example.elevatewebsolutions_tasktracker.auth.models.AuthenticationResult;
import com.example.elevatewebsolutions_tasktracker.auth.models.LoginRequest;
import com.example.elevatewebsolutions_tasktracker.auth.utils.PasswordUtils;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;
import com.example.elevatewebsolutions_tasktracker.database.UserDAO;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Core authentication service for user login and validation
 * Handles password verification and database interaction
 */
public class UserAuthenticationService {
    private static final String TAG = "UserAuthService";

    private final UserDAO userDAO;
    private final ExecutorService executorService;

    public UserAuthenticationService(Context context) {
        TaskManagerDatabase database = TaskManagerDatabase.getDatabase(context);
        this.userDAO = database.userDAO();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Authenticates a user with username and password
     * @param loginRequest Contains username and password
     * @return CompletableFuture with AuthenticationResult
     */
    public CompletableFuture<AuthenticationResult> authenticateUser(LoginRequest loginRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "Starting authentication for username: " + loginRequest.getUsername());

                // Validate input
                if (!loginRequest.isValid()) {
                    Log.w(TAG, "Invalid login request: empty username or password");
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.INVALID_INPUT,
                        "Username and password are required"
                    );
                }

                // Validate username format - temporarily disable strict validation for debugging
                if (!PasswordUtils.isValidUsername(loginRequest.getUsername())) {
                    Log.w(TAG, "Invalid username format: " + loginRequest.getUsername());
                    // For now, let's allow it to continue and see if the user exists
                    // return new AuthenticationResult(
                    //     AuthenticationResult.AuthError.INVALID_INPUT,
                    //     "Invalid username format"
                    // );
                }

                // Get user from database (synchronous call in background thread)
                Log.d(TAG, "Looking up user in database: " + loginRequest.getUsername());
                User user = getUserByUsernamSync(loginRequest.getUsername());

                if (user == null) {
                    Log.w(TAG, "User not found in database: " + loginRequest.getUsername());
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.USER_NOT_FOUND,
                        "Invalid username or password"
                    );
                }

                Log.d(TAG, "User found: " + user.getUsername() + ", stored password: " + user.getPassword());
                Log.d(TAG, "Attempting to verify password: " + loginRequest.getPassword());

                // Verify password
                boolean passwordValid = verifyUserPassword(user, loginRequest.getPassword());

                if (!passwordValid) {
                    Log.w(TAG, "Password verification failed for user: " + loginRequest.getUsername());
                    Log.d(TAG, "Stored password: '" + user.getPassword() + "', Provided: '" + loginRequest.getPassword() + "'");
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.INVALID_CREDENTIALS,
                        "Invalid username or password"
                    );
                }

                Log.i(TAG, "User authenticated successfully: " + user.getUsername() +
                           " (Role: " + user.getUserRole().getDisplayName() + ")");

                return new AuthenticationResult(user);

            } catch (Exception e) {
                Log.e(TAG, "Authentication error", e);
                return new AuthenticationResult(
                    AuthenticationResult.AuthError.UNKNOWN_ERROR,
                    "Authentication failed due to system error"
                );
            }
        }, executorService);
    }

    /**
     * Verifies user password against stored hash
     * @param user The user to verify
     * @param password Plain text password to verify
     * @return true if password is correct
     */
    private boolean verifyUserPassword(User user, String password) {
        if (user.getPasswordSalt() == null) {
            // Handle users created before salt implementation
            // For now, do simple comparison (should be updated to rehash with salt)
            Log.w(TAG, "User has no salt, using legacy verification for: " + user.getUsername());
            return user.getPassword().equals(password);
        }

        return PasswordUtils.verifyPassword(password, user.getPassword(), user.getPasswordSalt());
    }

    /**
     * Gets user by username synchronously (for use in background thread)
     * @param username Username to search for
     * @return User object or null if not found
     */
    private User getUserByUsernamSync(String username) {
        try {
            return userDAO.getUserByUsernameSync(username);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by username: " + username, e);
            return null;
        }
    }

    /**
     * Creates a new user with hashed password
     * @param username Username for new user
     * @param password Plain text password
     * @param title User title/role description
     * @param isAdmin Whether user should have admin privileges
     * @return CompletableFuture with AuthenticationResult
     */
    public CompletableFuture<AuthenticationResult> createUser(String username, String password, String title, boolean isAdmin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate input
                if (!PasswordUtils.isValidUsername(username)) {
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.INVALID_INPUT,
                        "Invalid username format"
                    );
                }

                if (!PasswordUtils.isValidPassword(password)) {
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.INVALID_INPUT,
                        "Password must be at least 6 characters long"
                    );
                }

                // Check if user already exists
                User existingUser = getUserByUsernamSync(username);
                if (existingUser != null) {
                    Log.w(TAG, "User already exists: " + username);
                    return new AuthenticationResult(
                        AuthenticationResult.AuthError.INVALID_INPUT,
                        "Username already exists"
                    );
                }

                // Hash password with salt
                String[] hashAndSalt = PasswordUtils.hashPasswordWithNewSalt(password);
                String hashedPassword = hashAndSalt[0];
                String salt = hashAndSalt[1];

                // Create new user
                User newUser = new User(username, hashedPassword, title, isAdmin);
                newUser.setPasswordSalt(salt);

                // Save to database
                userDAO.insert(newUser);

                Log.i(TAG, "User created successfully: " + username +
                           " (Role: " + newUser.getUserRole().getDisplayName() + ")");

                return new AuthenticationResult(newUser);

            } catch (Exception e) {
                Log.e(TAG, "Error creating user", e);
                return new AuthenticationResult(
                    AuthenticationResult.AuthError.UNKNOWN_ERROR,
                    "Failed to create user due to system error"
                );
            }
        }, executorService);
    }

    /**
     * Validates user credentials without creating a session
     * @param username Username to validate
     * @param password Password to validate
     * @return CompletableFuture with boolean result
     */
    public CompletableFuture<Boolean> validateCredentials(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        return authenticateUser(request).thenApply(AuthenticationResult::isSuccess);
    }

    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
