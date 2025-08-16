package com.example.elevatewebsolutions_tasktracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityUserManagementBinding;

public class UserManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "UserManagementActivity";

    private ActivityUserManagementBinding binding;
    private TaskManagerRepository repository;
    private String buttonTitle;
    User tempUser;

    private SessionManager sessionManager;

    private int userId = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            buttonTitle = extras.getString("Button_Title", "Manage Users");
            userId = extras.getInt("User_Id", -1);
            Log.i("UserManagementActivity", "Button Title: " + buttonTitle);

        }

        repository = TaskManagerRepository.getRepository(getApplication());
        sessionManager = new SessionManager(this);

        if(userId != -1) {
            // If userId is provided, we are in update mode
            assert repository != null;
            //get user from the repository
            repository.getUserByUserId(userId).observe(this, user ->{
                //update the UI with user details
                binding.userNameEditText.setText(user.getUsername());
                binding.passwordEditText.setText(user.getPassword());
                binding.confirmPasswordEditText.setText(user.getPassword());
                binding.usertitleEditText.setText(user.getTitle());
                binding.adminSwitch.setChecked(user.getAdmin());
            });

            // Set the button text and click listener for update
            binding.addUserButton.setText("Update User");
            binding.addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUser(userId);
                }
            });
            //set the button text and click listener for delete
            binding.deleteUserButton.setVisibility(View.VISIBLE);
            binding.deleteUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(userId);
                }
            });
        } else {
            // If no userId is provided, we are in add mode
            Log.i(TAG, "No User ID provided, adding new user");
            // Set the button text and click listener for add
            binding.addUserButton.setText("Add User");
            binding.addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle add user action
                    Log.i(TAG, "Add User Button Clicked");
                    addUser();
                }
            });
            binding.deleteUserButton.setVisibility(View.GONE);
        }

        binding.returnToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the main activity
                navigateToSettings();
            }
        });
    }

    private void deleteUser(int userId) {
        repository.getUserByUserId(userId).observe(this, user -> {
            //Display a confirmation dialog before deleting the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UserManagementActivity.this);
            final AlertDialog alertDialog = alertBuilder.create();

            alertBuilder.setMessage("Delete user " + user.getUsername() + "?");

            alertBuilder.setPositiveButton("Delete?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Delete the user with the given userId
                    repository.deleteUser(user);
                    toastMaker("User " + user.getUsername() + " deleted successfully.");
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });

            alertBuilder.create().show();
        });
    }

    private void addUser() {
        // Get the input values from the EditText fields
        String username = binding.userNameEditText.getText().toString().trim();
        String title = binding.usertitleEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        boolean isAdmin = binding.adminSwitch.isChecked();

        // Validate the input values
        if(!password.equals(confirmPassword)){
            toastMaker("Passwords do not match");
            binding.confirmPasswordEditText.setError("Passwords do not match");
        } else if(username.isEmpty() || password.isEmpty()) {
            toastMaker("Username or password cannot be empty");
            binding.userNameEditText.setError("Username cannot be empty");
            binding.passwordEditText.setError("Password cannot be empty");
        } else {
            // Proceed with adding the user
            toastMaker("Adding user: " + username);
            // add the user to the database
            repository.insertUser(new User(username, password, title, isAdmin));
        }
        navigateToSettings();
    }

    private void updateUser(int userId) {
        // Get the input values from the EditText fields
        String username = binding.userNameEditText.getText().toString().trim();
        String title = binding.usertitleEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        boolean admin = binding.adminSwitch.isChecked();

        // Validate the input values
        if(!password.equals(confirmPassword)){
            toastMaker("Passwords do not match");
            binding.confirmPasswordEditText.setError("Passwords do not match");
        } else if(username.isEmpty() || password.isEmpty()) {
            toastMaker("Username or password cannot be empty");
            binding.userNameEditText.setError("Username cannot be empty");
            binding.passwordEditText.setError("Password cannot be empty");
        } else {
            // Proceed with updating the user
            //toastMaker("Updating user: " + username);
            // update the user in the database
            User user = new User(username, password, title, admin);
            user.setId(userId);
            repository.updateUser(user);
            toastMaker("Updating user: " + username);
            toastMaker("User not found for update");
            // Return to the settings activity after updating
            navigateToSettings();
        }
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSettings() {
        Intent intent = SettingsActivity.settingsIntentFactory(UserManagementActivity.this,  sessionManager.getCurrentUserId());
        startActivity(intent);
    }

    public static Intent userManagementIntentFactory(Context context, String buttonTitle, int userId) {
        Intent intent = new Intent(context, UserManagementActivity.class);
        intent.putExtra("Button_Title", buttonTitle);
        intent.putExtra("User_Id", userId);
        return intent;
    }
}