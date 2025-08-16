package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TASK_MANAGER";

    private SessionManager sessionManager;
    private TextView usernameDisplayTextView;
    private Button logoutButton;

    private ActivityMainBinding binding;

    public static Intent mainActivityIntentFactory(Context applicationContext, int userId) {
        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.putExtra("USER_ID", userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize repository early to ensure database and users are created
        TaskManagerRepository repository = TaskManagerRepository.getRepository(getApplication());

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // User not logged in, redirect to login
            Intent loginIntent = LoginActivity.loginIntentFactory(this);
            startActivity(loginIntent);
            finish(); // Close MainActivity so user can't go back
            return;
        }

        // Initialize UI components
        initializeViews();
        setupLogoutButton();
        displayCurrentUser();

        // Log current user info for debugging
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            android.util.Log.i(TAG, "User logged in: " + currentSession.getUsername() +
                    " (Admin: " + currentSession.isAdmin() + ")");
        }
    }

    private void initializeViews() {
        usernameDisplayTextView = findViewById(R.id.usernameDisplayTextView);
        logoutButton = findViewById(R.id.logoutButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

   @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu and set visibility based on user session
        MenuItem item = menu.findItem(R.id.settingsMenuItem);
        UserSession currentSession = sessionManager.getCurrentSession();
        if(currentSession == null){
            return false; // No session, don't show menu item
        }
        item.setVisible(currentSession.isAdmin());

       item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
               Intent intent = SettingsActivity.settingsIntentFactory((getApplicationContext()), sessionManager.getCurrentUserId());
               startActivity(intent);
               return false;
           }
       });

        return true;
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }

    private void displayCurrentUser() {
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            String displayText = "Logged in as: " + currentSession.getUsername();
            if (currentSession.isAdmin()) {
                displayText += " (Admin)";
                // Show admin-specific UI elements
                binding.userHeader.setVisibility(View.VISIBLE);
                binding.user1.setVisibility(View.VISIBLE);
                binding.addTaskButton.setVisibility(View.VISIBLE);
            }
            usernameDisplayTextView.setText(displayText);
            // Update options menu to reflect current user
            invalidateOptionsMenu();
        } else {
            usernameDisplayTextView.setText("Not logged in");
        }
    }

    private void performLogout() {
        // Log the logout action
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            android.util.Log.i(TAG, "User logging out: " + currentSession.getUsername());
        }

        // Clear the session
        sessionManager.destroySession();

        // Redirect to login activity
        Intent loginIntent = LoginActivity.loginIntentFactory(this);
        startActivity(loginIntent);
        finish(); // Close MainActivity so user can't go back
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check session validity when activity resumes
        if (!sessionManager.isLoggedIn()) {
            // Session expired, redirect to login
            Intent loginIntent = LoginActivity.loginIntentFactory(this);
            startActivity(loginIntent);
            finish();
        }
    }



}