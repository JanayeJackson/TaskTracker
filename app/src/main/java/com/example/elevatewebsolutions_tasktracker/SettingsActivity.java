package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityMainBinding;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "TASK_MANAGER_SETTINGS";

    private ActivitySettingsBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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