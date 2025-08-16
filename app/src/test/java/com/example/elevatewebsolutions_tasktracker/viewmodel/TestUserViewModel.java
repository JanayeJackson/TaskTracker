package com.example.elevatewebsolutions_tasktracker.viewmodel;

/**
 * testable version of userviewmodel for unit testing
 * uses pure java objects without android framework dependencies
 */
public class TestUserViewModel {

    private boolean isLoggedIn = false;
    private MockUserSession currentUser = null;
    private String errorMessage = null;

    /**
     * simulate successful login for testing
     */
    public void simulateSuccessfulLogin(String username) {
        // clear any previous error
        errorMessage = null;

        // create mock user session
        MockUserSession session = new MockUserSession(username, false, 1);

        // update state
        currentUser = session;
        isLoggedIn = true;
    }

    /**
     * simulate admin login for testing
     */
    public void simulateAdminLogin(String username) {
        // clear any previous error
        errorMessage = null;

        // create mock admin session
        MockUserSession session = new MockUserSession(username, true, 1);

        // update state
        currentUser = session;
        isLoggedIn = true;
    }

    /**
     * simulate failed login for testing
     */
    public void simulateFailedLogin(String error) {
        errorMessage = error;
        isLoggedIn = false;
        currentUser = null;
    }

    /**
     * logout functionality
     */
    public void logout() {
        currentUser = null;
        isLoggedIn = false;
        errorMessage = null;
    }

    /**
     * clear error message
     */
    public void clearError() {
        errorMessage = null;
    }

    // simple getters for state - no livedata needed for unit tests
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public MockUserSession getCurrentUser() {
        return currentUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * get current user id (convenience method)
     */
    public int getCurrentUserId() {
        MockUserSession session = getCurrentUser();
        return session != null ? session.getUserId() : -1;
    }

    /**
     * check if current user is admin (convenience method)
     */
    public boolean isCurrentUserAdmin() {
        MockUserSession session = getCurrentUser();
        return session != null && session.isAdmin();
    }

    /**
     * simple mock user session for testing - no android dependencies
     */
    public static class MockUserSession {
        private final String username;
        private final boolean isAdmin;
        private final int userId;

        public MockUserSession(String username, boolean isAdmin, int userId) {
            this.username = username;
            this.isAdmin = isAdmin;
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public int getUserId() {
            return userId;
        }
    }
}
