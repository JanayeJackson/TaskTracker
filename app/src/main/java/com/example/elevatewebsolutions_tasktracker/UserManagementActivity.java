package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elevatewebsolutions_tasktracker.databinding.ActivityUserManagementBinding;

public class UserManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "UserManagementActivity";

    private ActivityUserManagementBinding binding;
    private String buttonTitle;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            buttonTitle = extras.getString("Button_Title", "Manage Users");
            Log.i("UserManagementActivity", "Button Title: " + buttonTitle);

        }
        
        binding.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonTitle.equalsIgnoreCase("Update User")) {
                    // Handle manage users action
                    Log.i(TAG, "Manage Users Button Clicked");
                    updateUser();
                } else if(buttonTitle.equalsIgnoreCase("Add User")) {
                    // Handle add user action
                    Log.i(TAG, "Add User Button Clicked");
                    addUser();
                }else if(buttonTitle.equalsIgnoreCase("Delete User")) {
                    // Handle delete user action
                    Log.i(TAG, "Delete User Button Clicked");
                    deleteUser();
                } else {
                    Log.i(TAG, "Unknown button title: " + buttonTitle);
                }
                // Handle add user button click
                Log.i(TAG, "Add User Button Clicked");
                // You can start another activity or perform an action here
            }
        });
    }

    private void deleteUser() {
    }

    private void addUser() {
    }

    private void updateUser() {
    }

    public static Intent userManagementIntentFactory(Context context, String buttonTitle) {
        Intent intent = new Intent(context, UserManagementActivity.class);
        intent.putExtra("Button_Title", buttonTitle);
        return intent;
    }
}