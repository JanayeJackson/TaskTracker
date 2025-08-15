package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private TaskManagerRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize repository and database
        repository = TaskManagerRepository.getRepository(getApplication());
        sessionManager = new SessionManager(this);
        UserSession currentUserSession = sessionManager.getCurrentSession();

        // Check if user is an admin
        if (!currentUserSession.isAdmin()) {
            // User is not an admin, redirect to main activity
            toastMaker("You do not have permission to access settings.");
            navigateToMainActivity();
            return;
        }

        binding.listUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.returnToTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent mainIntent = MainActivity.mainActivityIntentFactory(this, sessionManager.getCurrentUserId());
        startActivity(mainIntent);
        finish(); // Close setting activity
    }

    /**
     * Displays a message
     * @param message to be displayed
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static Intent settingsIntentFactory(Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}