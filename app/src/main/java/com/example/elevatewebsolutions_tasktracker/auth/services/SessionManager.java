package com.example.elevatewebsolutions_tasktracker.auth.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

/*
// flow of session management:
// After successful authentication -> createSession(user)
// Throughout app -> isLoggedIn(), getCurrentUserId(), et al
// On logout -> destroySession()
// App restart -> automatic session restoration if valid
*/

/**
 * Manages user sessions using SharedPreferences
 * Handles login/logout, session persistence, and timeout management
 */
public class SessionManager {
    private static final String TAG = "SessionManager";

    // SharedPreferences file name and keys
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "is_admin";
    private static final String KEY_LOGIN_TIMESTAMP = "login_timestamp";
    private static final String KEY_EXPIRATION_TIMESTAMP = "expiration_timestamp";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private UserSession currentSession;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        // Load existing session if available
        loadSessionFromPreferences();
    }

    /**
     * Creates a new user session after successful login
     * @param user The authenticated user
     * @return Created UserSession
     */
    public UserSession createSession(User user) {
        if (user == null) {
            Log.e(TAG, "Cannot create session: user is null");
            return null;
        }

        UserSession session = new UserSession(user);
        this.currentSession = session;

        // Save session to SharedPreferences
        saveSessionToPreferences(session);

        Log.i(TAG, "Session created for user: " + user.getUsername() +
                   " (expires at: " + session.getExpirationTimestamp() + ")");

        return session;
    }

    /**
     * Destroys the current session (logout)
     */
    public void destroySession() {
        if (currentSession != null) {
            Log.i(TAG, "Session destroyed for user: " + currentSession.getUsername());
        }

        currentSession = null;
        clearSessionFromPreferences();
    }

    /**
     * Gets the current active session
     * @return Current UserSession or null if no valid session
     */
    public UserSession getCurrentSession() {
        if (currentSession != null && currentSession.isExpired()) {
            Log.i(TAG, "Session expired for user: " + currentSession.getUsername());
            destroySession();
            return null;
        }

        return currentSession;
    }

    /**
     * Checks if user is currently logged in with valid session
     * @return true if logged in with valid session
     */
    public boolean isLoggedIn() {
        UserSession session = getCurrentSession();
        return session != null && session.isValid();
    }

    /**
     * Gets the current user ID if logged in
     * @return User ID or -1 if not logged in
     */
    public int getCurrentUserId() {
        UserSession session = getCurrentSession();
        return session != null ? session.getUserId() : -1;
    }

    /**
     * Gets the current username if logged in
     * @return Username or null if not logged in
     */
    public String getCurrentUsername() {
        UserSession session = getCurrentSession();
        return session != null ? session.getUsername() : null;
    }

    /**
     * Checks if current user is admin
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        UserSession session = getCurrentSession();
        return session != null && session.isAdmin();
    }

    /**
     * Gets time remaining in current session (in milliseconds)
     * @return Time remaining or 0 if no valid session
     */
    public long getSessionTimeRemaining() {
        UserSession session = getCurrentSession();
        return session != null ? session.getTimeRemaining() : 0;
    }

    /**
     * Extends the current session (refreshes expiration time)
     * @return true if session was extended, false if no valid session
     */
    public boolean extendSession() {
        UserSession session = getCurrentSession();
        if (session == null) {
            return false;
        }

        // Create new session with extended time
        User mockUser = createMockUserFromSession(session);
        createSession(mockUser);

        Log.i(TAG, "Session extended for user: " + session.getUsername());
        return true;
    }

    /**
     * Saves session data to SharedPreferences
     * @param session Session to save
     */
    private void saveSessionToPreferences(UserSession session) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, session.getUserId());
        editor.putString(KEY_USERNAME, session.getUsername());
        editor.putBoolean(KEY_IS_ADMIN, session.isAdmin());
        editor.putLong(KEY_LOGIN_TIMESTAMP, session.getLoginTimestamp());
        editor.putLong(KEY_EXPIRATION_TIMESTAMP, session.getExpirationTimestamp());
        editor.apply();
    }

    /**
     * Loads session data from SharedPreferences
     */
    private void loadSessionFromPreferences() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
            String username = sharedPreferences.getString(KEY_USERNAME, null);
            boolean isAdmin = sharedPreferences.getBoolean(KEY_IS_ADMIN, false);
            long loginTimestamp = sharedPreferences.getLong(KEY_LOGIN_TIMESTAMP, 0);
            long expirationTimestamp = sharedPreferences.getLong(KEY_EXPIRATION_TIMESTAMP, 0);

            if (userId != -1 && username != null) {
                currentSession = new UserSession(userId, username, isAdmin, loginTimestamp, expirationTimestamp);

                // Check if session is still valid
                if (currentSession.isExpired()) {
                    Log.i(TAG, "Loaded session is expired, clearing it");
                    destroySession();
                } else {
                    Log.i(TAG, "Session loaded for user: " + username +
                               " (expires in: " + currentSession.getTimeRemaining() + "ms)");
                }
            }
        }
    }

    /**
     * Clears session data from SharedPreferences
     */
    private void clearSessionFromPreferences() {
        editor.clear();
        editor.apply();
    }

    /**
     * Creates a mock User object from session data for session extension
     * @param session Session to create user from
     * @return Mock User object
     */
    private User createMockUserFromSession(UserSession session) {
        User user = new User(session.getUsername(), "", "");
        user.setId(session.getUserId());
        user.setAdmin(session.isAdmin());
        return user;
    }

    /**
     * Validates session integrity and clears if corrupted
     * @return true if session is valid, false if cleared due to corruption
     */
    public boolean validateSessionIntegrity() {
        if (currentSession == null) {
            return true; // No session is valid state
        }

        // Check for basic data integrity
        if (currentSession.getUserId() <= 0 ||
            currentSession.getUsername() == null ||
            currentSession.getUsername().trim().isEmpty()) {

            Log.w(TAG, "Session integrity check failed, clearing session");
            destroySession();
            return false;
        }

        return true;
    }

    /**
     * Gets session statistics for debugging
     * @return String with session information
     */
    public String getSessionInfo() {
        UserSession session = getCurrentSession();
        if (session == null) {
            return "No active session";
        }

        return String.format(
            "Session Info - User: %s, ID: %d, Admin: %s, Time Remaining: %dms",
            session.getUsername(),
            session.getUserId(),
            session.isAdmin(),
            session.getTimeRemaining()
        );
    }
}
