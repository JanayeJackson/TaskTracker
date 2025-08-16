package com.example.elevatewebsolutions_tasktracker.viewmodel;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * unit tests for user login logic
 * tests login behavior without android framework dependencies
 */
public class UserViewModelLoginTest {

    private TestUserViewModel userViewModel;

    @Before
    public void setUp() {
        userViewModel = new TestUserViewModel();
    }

    @Test
    public void initialState_shouldBeLoggedOut() {
        // assert initial state
        assertFalse("should not be logged in initially", userViewModel.isLoggedIn());
        assertNull("should have no current user initially", userViewModel.getCurrentUser());
        assertNull("should have no error initially", userViewModel.getErrorMessage());
        assertEquals("should return -1 for user id when not logged in", -1, userViewModel.getCurrentUserId());
        assertFalse("should not be admin when not logged in", userViewModel.isCurrentUserAdmin());
    }

    @Test
    public void login_withValidCredentials_shouldSucceed() {
        // arrange
        String validUsername = "admin";

        // act - simulate successful login
        userViewModel.simulateSuccessfulLogin(validUsername);

        // assert
        assertTrue("should be logged in after successful login", userViewModel.isLoggedIn());
        assertNotNull("should have current user after login", userViewModel.getCurrentUser());
        assertEquals("should have correct username", validUsername, userViewModel.getCurrentUser().getUsername());
        assertNull("should have no error after successful login", userViewModel.getErrorMessage());
    }

    @Test
    public void login_withInvalidCredentials_shouldFail() {
        // arrange
        String errorMessage = "Invalid credentials";

        // act - simulate failed login
        userViewModel.simulateFailedLogin(errorMessage);

        // assert
        assertFalse("should not be logged in with invalid credentials", userViewModel.isLoggedIn());
        assertNull("should have no current user with invalid credentials", userViewModel.getCurrentUser());
        assertEquals("should show error message", errorMessage, userViewModel.getErrorMessage());
    }

    @Test
    public void login_withEmptyCredentials_shouldFail() {
        // arrange
        String expectedError = "Username and password are required";

        // act
        userViewModel.simulateFailedLogin(expectedError);

        // assert
        assertFalse("should not be logged in with empty credentials", userViewModel.isLoggedIn());
        assertEquals("should show validation error", expectedError, userViewModel.getErrorMessage());
    }

    @Test
    public void logout_shouldClearUserState() {
        // arrange - first login a user
        userViewModel.simulateSuccessfulLogin("testuser");
        assertTrue("should be logged in before logout", userViewModel.isLoggedIn());

        // act
        userViewModel.logout();

        // assert
        assertFalse("should not be logged in after logout", userViewModel.isLoggedIn());
        assertNull("should have no current user after logout", userViewModel.getCurrentUser());
        assertNull("should have no error after logout", userViewModel.getErrorMessage());
        assertEquals("should return -1 for user id after logout", -1, userViewModel.getCurrentUserId());
        assertFalse("should not be admin after logout", userViewModel.isCurrentUserAdmin());
    }

    @Test
    public void clearError_shouldResetErrorMessage() {
        // arrange - set error first
        String errorMessage = "Some error occurred";
        userViewModel.simulateFailedLogin(errorMessage);
        assertEquals("should have error before clearing", errorMessage, userViewModel.getErrorMessage());

        // act
        userViewModel.clearError();

        // assert
        assertNull("error should be cleared", userViewModel.getErrorMessage());
    }

    @Test
    public void getCurrentUserId_withNoUser_shouldReturnMinusOne() {
        // arrange - ensure no user is logged in
        userViewModel.logout();

        // act & assert
        assertEquals("should return -1 when no user logged in", -1, userViewModel.getCurrentUserId());
    }

    @Test
    public void isCurrentUserAdmin_withAdminUser_shouldReturnTrue() {
        // arrange
        userViewModel.simulateAdminLogin("admin");

        // act & assert
        assertTrue("should recognize admin user", userViewModel.isCurrentUserAdmin());
    }

    @Test
    public void isCurrentUserAdmin_withRegularUser_shouldReturnFalse() {
        // arrange
        userViewModel.simulateSuccessfulLogin("regularuser");

        // act & assert
        assertFalse("should not recognize regular user as admin", userViewModel.isCurrentUserAdmin());
    }

    @Test
    public void multipleLoginAttempts_shouldUpdateStateCorrectly() {
        // act - multiple login attempts
        userViewModel.simulateFailedLogin("First error");
        assertEquals("should show first error", "First error", userViewModel.getErrorMessage());

        userViewModel.simulateSuccessfulLogin("user1");
        assertTrue("should be logged in after success", userViewModel.isLoggedIn());

        userViewModel.logout();
        assertFalse("should be logged out after logout", userViewModel.isLoggedIn());

        // assert - should handle multiple state changes without issues
        assertNull("should have no current user after final logout", userViewModel.getCurrentUser());
    }
}
