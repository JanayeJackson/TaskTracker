package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.EntityUpsertAdapter;

import com.example.elevatewebsolutions_tasktracker.adapter.TaskAdapter;
import com.example.elevatewebsolutions_tasktracker.adapter.UserAdapter;
import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivitySettingsBinding;
import com.example.elevatewebsolutions_tasktracker.viewmodel.TaskListViewModel;
import com.example.elevatewebsolutions_tasktracker.viewmodel.UserViewModel;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize session manager and get current user session
        sessionManager = new SessionManager(this);
        UserSession currentUserSession = sessionManager.getCurrentSession();

        if(currentUserSession == null){
            // No user session, redirect to main activity
            toastMaker("You must be logged in to access settings.");
            navigateToMainActivity();
            return;
        }

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
                Intent intent = ListUserActivity.listUserIntentFactory(SettingsActivity.this);
                startActivity(intent);
                //updateDisplay();
                //setupUserList();
            }
        });

        binding.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = UserManagementActivity.userManagementIntentFactory(SettingsActivity.this, -1);
                startActivity(intent);
            }
        });

        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddTaskActivity.addTaskActivityIntentFactory(SettingsActivity.this);
                startActivity(intent);
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

    public static Intent settingsIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("USER_ID", userId);
        return intent;
    }
}