package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elevatewebsolutions_tasktracker.auth.models.AuthenticationResult;
import com.example.elevatewebsolutions_tasktracker.auth.models.LoginRequest;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.auth.validation.InputValidator;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TaskManagerRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize repository and database early to ensure dummy users are created
        repository = TaskManagerRepository.getRepository(getApplication());
        sessionManager = new SessionManager(this);

        // Force database initialization to ensure users exist before any login attempts
        // This is done asynchronously but will complete before user can type and click login
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            // This will trigger database creation and user setup if needed
            repository.getUserByUserName("admin"); // Dummy call to initialize DB
        });

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            // User already logged in, go to main activity
            navigateToMainActivity();
            return;
        }

        binding.loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                authenticateUser();
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = UserManagementActivity.userManagementIntentFactory(LoginActivity.this, -1);
                startActivity(intent);
            }
        });
    }

    /**
     * Authenticates user using the new authentication system
     */
    private void authenticateUser(){
        String username = binding.userNameLoginEditText.getText().toString().trim();
        String password = binding.passwordLoginEditText.getText().toString();

        // Validate input
        InputValidator.ValidationResult validation = InputValidator.validateLoginRequest(username, password);
        if (!validation.isValid()) {
            toastMaker(validation.getFirstError());
            return;
        }

        // Show loading state
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText("Logging in...");

        // Create login request
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Authenticate using repository
        repository.authenticateUser(username, password)
            .thenAccept(result -> {
                runOnUiThread(() -> {
                    // Reset button state
                    binding.loginButton.setEnabled(true);
                    binding.loginButton.setText("Login");

                    if (result.isSuccess()) {
                        // Authentication successful
                        sessionManager.createSession(result.getUser());
                        toastMaker("Welcome, " + result.getUser().getUsername() + "!");
                        navigateToMainActivity();
                    } else {
                        // Authentication failed
                        toastMaker(result.getErrorMessage());
                        clearPasswordField();
                    }
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    // Reset button state
                    binding.loginButton.setEnabled(true);
                    binding.loginButton.setText("Login");

                    toastMaker("Login failed: " + throwable.getMessage());
                    clearPasswordField();
                });
                return null;
            });
    }

    /**
     * Navigate to MainActivity
     */
    private void navigateToMainActivity() {
        Intent mainIntent = MainActivity.mainActivityIntentFactory(this, sessionManager.getCurrentUserId());
        startActivity(mainIntent);
        finish(); // Close login activity
    }

    /**
     * Clear password field for security
     */
    private void clearPasswordField() {
        binding.passwordLoginEditText.setText("");
        binding.passwordLoginEditText.requestFocus();
    }

    /**
     * Displays a message
     * @param message to be displayed
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates intent of LoginActivity
     * @param context of current application running
     * @return Intent of LoginActivity
     */
    public static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}