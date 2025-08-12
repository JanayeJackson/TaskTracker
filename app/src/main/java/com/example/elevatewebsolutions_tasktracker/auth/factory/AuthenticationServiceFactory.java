package com.example.elevatewebsolutions_tasktracker.auth.factory;

import android.content.Context;

import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.auth.services.UserAuthenticationService;

/**
 * Simple factory for creating and managing authentication services
 * Provides easy access to authentication components throughout the app
 */
public class AuthenticationServiceFactory {
    private static AuthenticationServiceFactory instance;
    private UserAuthenticationService authService;
    private SessionManager sessionManager;

    private AuthenticationServiceFactory() {
        // Private constructor for singleton
    }

    public static AuthenticationServiceFactory getInstance() {
        if (instance == null) {
            instance = new AuthenticationServiceFactory();
        }
        return instance;
    }

    /**
     * Initializes authentication services with application context
     * @param context Application context
     */
    public void initialize(Context context) {
        if (authService == null) {
            authService = new UserAuthenticationService(context);
        }
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
    }

    /**
     * Gets the authentication service instance
     * @return UserAuthenticationService instance
     */
    public UserAuthenticationService getAuthenticationService() {
        return authService;
    }

    /**
     * Gets the session manager instance
     * @return SessionManager instance
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Checks if services are initialized
     * @return true if services are ready to use
     */
    public boolean isInitialized() {
        return authService != null && sessionManager != null;
    }

    /**
     * Shuts down authentication services
     */
    public void shutdown() {
        if (authService != null) {
            authService.shutdown();
        }
        authService = null;
        sessionManager = null;
    }
}
