package com.example.elevatewebsolutions_tasktracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityUserManagementBinding;

public class UserManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "UserManagementActivity";

    private ActivityUserManagementBinding binding;
    private TaskManagerRepository repository;

    private SessionManager sessionManager;

    private LiveData<User> userLiveData;
    private Observer<User> userObserver;

    private int userId = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("User_Id", -1);
        }

        repository = TaskManagerRepository.getRepository(getApplication());
        sessionManager = new SessionManager(this);


        if(userId != -1) {
            // If userId is provided, we are in update mode
            assert repository != null;
            //get user from the repository
            updateDisplay();
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
            binding.deleteUserButton.setOnClickListener(v -> {
                /*@Override
                public void onClick(View view) {
                    deleteUser(userId);
                }*/
                repository.deleteUserById(userId);
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

        UserSession currentSession = sessionManager.getCurrentSession();
        if(currentSession != null && currentSession.isAdmin()) {
            binding.adminSwitch.setVisibility(View.VISIBLE);
            binding.returnToSettingsButton.setVisibility(View.VISIBLE);
        } else {
            binding.adminSwitch.setVisibility(View.GONE);
            binding.returnToSettingsButton.setVisibility(View.GONE);
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
        try{
                //Display a confirmation dialog before deleting the user
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UserManagementActivity.this);

                alertBuilder.setMessage("Delete user ?");

                alertBuilder.setPositiveButton("Delete?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete the user with the given userId
                        repository.deleteUserById(userId);

                        runOnUiThread(() -> {
                            toastMaker("User deleted successfully.");
                            navigateToSettings();
                            finish(); // close this activity
                        });
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertBuilder.create().show();

            } catch (Exception e) {
                Log.e(TAG, "Error deleting user: " + e.getMessage());
                toastMaker("Error deleting user: " + e.getMessage());
            }
    }

    private void addUser() {
        // Get the input values from the EditText fields
        boolean isAdmin;
        String username = binding.userNameEditText.getText().toString().trim();
        String title = binding.usertitleEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        if(binding.adminSwitch.getVisibility() != View.VISIBLE) {
            isAdmin = false;
        }else{
            isAdmin = binding.adminSwitch.isChecked();
        }

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

    private void updateDisplay() {
        userLiveData = repository.getUserByUserId(userId);

        userObserver = user -> {
            if(isFinishing() || isDestroyed()) {
                return;
            }

            if (user == null) {
                // The user was deleted (or doesn't exist). Stop observing and leave.
                if (userLiveData != null) userLiveData.removeObserver(userObserver);
                Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                navigateToSettings();
                return;
            }
            //update the UI with user details
            binding.userNameEditText.setText(user.getUsername());
            binding.passwordEditText.setText(user.getPassword());
            binding.confirmPasswordEditText.setText(user.getPassword());
            binding.usertitleEditText.setText(user.getTitle());
            binding.adminSwitch.setChecked(user.getAdmin());

        };
        userLiveData.observe(this, userObserver);
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
            repository.getUserByUserId(userId).observe(this, user -> {;
                if (user != null) {
                    Log.i(TAG, "User found for update: " + user.getUsername());
                    user.setUsername(username);
                    user.setTitle(title);
                    user.setPassword(password);
                    user.setAdmin(admin);
                    repository.updateUser(user);
                    toastMaker("Updating user: " + username);
                    toastMaker("User not found for update");
                    // Return to the settings activity after updating
                    navigateToSettings();
                } else {
                    Log.w(TAG, "User not found for update with ID: " + userId);
                    // Return to the settings activity after updating
                    navigateToSettings();
                }
                repository.getUserByUserId(userId).removeObservers(UserManagementActivity.this);
            });

        }
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSettings() {
        Intent intent = SettingsActivity.settingsIntentFactory(UserManagementActivity.this,  sessionManager.getCurrentUserId());
        startActivity(intent);
    }

    public static Intent userManagementIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, UserManagementActivity.class);
        intent.putExtra("User_Id", userId);
        return intent;
    }
}