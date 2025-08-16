package com.example.elevatewebsolutions_tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elevatewebsolutions_tasktracker.adapter.UserAdapter;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityListUserBinding;
import com.example.elevatewebsolutions_tasktracker.viewmodel.UserViewModel;

public class ListUserActivity extends AppCompatActivity {

    private ActivityListUserBinding binding;

    private UserAdapter userAdapter;
    private UserViewModel userViewModel;

    private SessionManager sessionManager;

    public static Intent listUserIntentFactory(SettingsActivity settingsActivity) {
        return new Intent(settingsActivity, ListUserActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeDisplay();
        setupUserList();
        sessionManager = new SessionManager(this);

        binding.returnToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SettingsActivity.settingsIntentFactory(ListUserActivity.this, sessionManager.getCurrentUserId());
                startActivity(intent);
            }
        });
    }

    private void initializeDisplay() {

        // create adapter with click listener for task interaction
        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                handleUserClick(user);
            }
        });

        binding.userRecyclerView.setAdapter(userAdapter);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    private void setupUserList() {
        userViewModel.getUserList().observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                userAdapter.updateUserList(users);
                binding.userRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.userRecyclerView.setVisibility(View.GONE);
                toastMaker("No users found.");
            }
        });
    }

    private void handleUserClick(User user) {
        // Handle user click, e.g., navigate to user management activity
        Intent intent = UserManagementActivity.userManagementIntentFactory(this, "Update User", user.getId());
        startActivity(intent);
    }

    /**
     * Displays a message
     * @param message to be displayed
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}