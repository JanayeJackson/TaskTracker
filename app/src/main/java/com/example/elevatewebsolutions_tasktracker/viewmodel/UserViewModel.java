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

    }

    public LiveData<Boolean> getIsLoggedIn() {
    }

    public LiveData<UserSession> getCurrentUser() {
    }

    public LiveData<String> getErrorMessage() {
    }


    public void login(String username, String password) {

    }


    public void logout() {

    }


    private void checkExistingSession() {



    public int getCurrentUserId() {

    }


    public boolean isCurrentUserAdmin() {

    }


    public void clearError() {
    }
}
