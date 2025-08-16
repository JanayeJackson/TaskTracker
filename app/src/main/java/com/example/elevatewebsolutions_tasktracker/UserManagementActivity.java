package com.example.elevatewebsolutions_tasktracker;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityUserManagementBinding;

public class UserManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "UserManagementActivity";

    private ActivityUserManagementBinding binding;
    private TaskManagerRepository repository;
    private String buttonTitle;
    

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int userId = -1;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            buttonTitle = extras.getString("Button_Title", "Manage Users");
            userId = extras.getInt("User_Id", -1);
            Log.i("UserManagementActivity", "Button Title: " + buttonTitle);

        }

        repository = TaskManagerRepository.getRepository(getApplication());
         // Default value for userId

        if(userId != -1) {
            // If userId is provided, we are in update mode
            assert repository != null;
            User user = repository.getUserByUserId(userId).getValue();
            assert user != null;
            binding.userNameEditText.setText(user.getUsername());
            binding.passwordEditText.setText(user.getPassword());
            binding.usertitleEditText.setText(user.getTitle());
            binding.adminSwitch.setChecked(user.getAdmin());


            binding.addUserButton.setText("Update User");
            binding.addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUser(user);
                }
            });

            binding.deleteUserButton.setVisibility(View.VISIBLE);
            binding.deleteUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(user);
                }
            });
        } else {
            // If no userId is provided, we are in add mode
            Log.i(TAG, "No User ID provided, adding new user");
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
    }

    private void deleteUser(User user) {
        //Delete the user with the given userId
        repository.deleteUser(user);
    }

    private void addUser() {
        String username = binding.userNameEditText.getText().toString().trim();
        //String title = binding.titleEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        boolean isAdmin = binding.adminSwitch.isChecked();

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
            repository.insertUser(new User(username, password, " ", isAdmin));
        }
    }

    private void updateUser(User user) {
        String username = binding.userNameEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        boolean admin = binding.adminSwitch.isChecked();

        if(!password.equals(confirmPassword)){
            toastMaker("Passwords do not match");
            binding.confirmPasswordEditText.setError("Passwords do not match");
        } else if(username.isEmpty() || password.isEmpty()) {
            toastMaker("Username or password cannot be empty");
            binding.userNameEditText.setError("Username cannot be empty");
            binding.passwordEditText.setError("Password cannot be empty");
        } else {
            // Proceed with updating the user
            toastMaker("Updating user: " + username);
            // update the user in the database
            assert user != null;
            user.setUsername(username);
            user.setPassword(password);
            user.setTitle("");
            user.setAdmin(admin);
            repository.updateUser(user);
        }
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static Intent userManagementIntentFactory(Context context, String buttonTitle) {
        Intent intent = new Intent(context, UserManagementActivity.class);
        intent.putExtra("Button_Title", buttonTitle);
        return intent;
    }
}