package com.example.elevatewebsolutions_tasktracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.models.LoginRequest;
import com.example.elevatewebsolutions_tasktracker.auth.models.AuthenticationResult;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.auth.services.UserAuthenticationService;


public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";

    private final SessionManager sessionManager;
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


    public void logout() {
        sessionManager.destroySession();

        // update livedata
        currentUser.setValue(null);
        isLoggedIn.setValue(false);
        errorMessage.setValue(null);
    }


    private void checkExistingSession() {
        if (sessionManager.isLoggedIn()) {
            UserSession session = sessionManager.getCurrentSession();
            if (session != null) {
                currentUser.setValue(session);
                isLoggedIn.setValue(true);
            } else {
                // when session is exp
                isLoggedIn.setValue(false);
            }
        } else {
            isLoggedIn.setValue(false);
        }
    }


    public int getCurrentUserId() {
        UserSession session = currentUser.getValue();
        return session != null ? session.getUserId() : -1;
    }


    public boolean isCurrentUserAdmin() {
        UserSession session = currentUser.getValue();
        return session != null && session.isAdmin();
    }


    public void clearError() {
        errorMessage.setValue(null);
    }
}
