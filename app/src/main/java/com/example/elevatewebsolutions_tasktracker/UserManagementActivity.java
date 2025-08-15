package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elevatewebsolutions_tasktracker.databinding.ActivityUserManagementBinding;

public class UserManagementActivity extends AppCompatActivity {

    private ActivityUserManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public static Intent userManagementIntentFactory(Context context, String buttonTitle) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("Button_Title", buttonTitle);
        return intent;
    }
}