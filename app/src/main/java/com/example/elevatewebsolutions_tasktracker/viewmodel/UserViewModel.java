package com.example.elevatewebsolutions_tasktracker.viewmodel;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.models.LoginRequest;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.auth.services.UserAuthenticationService;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import java.util.List;

/**
 * ViewModel for managing user authentication state and session management
 * Survives configuration changes and provides LiveData for UI observation
 */
public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";

    private final SessionManager sessionManager;
    private TaskManagerRepository repository;
    private final UserAuthenticationService authenticationService;

    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<UserSession> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);

        // init
        sessionManager = new SessionManager(application);
        authenticationService = new UserAuthenticationService(application);

        checkExistingSession();
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public LiveData<UserSession> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<User>> getUserList() {
        repository = TaskManagerRepository.getRepository(getApplication());

        assert repository != null;
        return repository.getAllUsers();
    }

    /**
     * Attempt to log in with username and password
     */
    public void login(String username, String password) {
        errorMessage.setValue(null);
        LoginRequest loginRequest = new LoginRequest(username, password);

        authenticationService.authenticateUser(loginRequest)
                .thenAccept(result -> {
                    if (result.isSuccess()) {
                        // login successful - create session
                        UserSession session = sessionManager.createSession(result.getUser());

                        // update livedata on main thread
                        currentUser.postValue(session);
                        isLoggedIn.postValue(true);
                    } else {
                        // login failed - update error message
                        errorMessage.postValue(result.getErrorMessage());
                        isLoggedIn.postValue(false);
                    }
                })
                .exceptionally(throwable -> {
                    errorMessage.postValue("Login failed: " + throwable.getMessage());
                    isLoggedIn.postValue(false);
                    return null;
                });
    }

    /**
     * Log out the current user
     */
    public void logout() {
        sessionManager.destroySession();

        // Update LiveData
        currentUser.setValue(null);
        isLoggedIn.setValue(false);
        errorMessage.setValue(null);
    }

    /**
     * Check if there's an existing valid session on app start
     */
    private void checkExistingSession() {
        if (sessionManager.isLoggedIn()) {
            UserSession session = sessionManager.getCurrentSession();
            if (session != null) {
                currentUser.setValue(session);
                isLoggedIn.setValue(true);
            } else {
                // Session expired - already cleared by SessionManager
                isLoggedIn.setValue(false);
            }
        } else {
            isLoggedIn.setValue(false);
        }
    }

    /**
     * Get current user ID (convenience method)
     */
    public int getCurrentUserId() {
        UserSession session = currentUser.getValue();
        return session != null ? session.getUserId() : -1;
    }

    /**
     * Check if current user is admin (convenience method)
     */
    public boolean isCurrentUserAdmin() {
        UserSession session = currentUser.getValue();
        return session != null && session.isAdmin();
    }

    /**
     * Clear error message
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
}
