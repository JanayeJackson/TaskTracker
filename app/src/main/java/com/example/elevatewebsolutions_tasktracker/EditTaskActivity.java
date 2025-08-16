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

import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityEditTaskBinding;

public class EditTaskActivity extends AppCompatActivity {

    private ActivityEditTaskBinding binding;
    private TaskManagerRepository repository;
    SessionManager sessionManager;

    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            taskId = extras.getInt("Task_Id", -1);
            if(taskId == -1) {
                toastMaker("Unable to find task");
                navigateToMainActivity();
            }
        }else{
            toastMaker("No task ID provided");
            navigateToMainActivity();
        }

        repository = TaskManagerRepository.getRepository(getApplication());

        populateEditTaskView(taskId);
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask(taskId);
            }
        });

        binding.cancelButton.setOnClickListener(view -> {
            navigateToMainActivity();
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask(taskId);
            }
        });
    }

    private void updateTask(int taskId) {
        // get form values
        String title = binding.taskDescriptionEditText.getText().toString().trim();
        String description = binding.taskDescriptionEditText.getText().toString().trim();
        String status = binding.taskStatusSpinner.getSelectedItem().toString();

        // validate title (required field)
        if (title.isEmpty()) {
            Toast.makeText(this, "Task title is required", Toast.LENGTH_SHORT).show();
            binding.taskDescriptionEditText.requestFocus();
            return;
        }

        // validate title length (max 100 chars)
        if (title.length() > 100) {
            Toast.makeText(this, "Title too long (max 100 characters)", Toast.LENGTH_SHORT).show();
            binding.taskDescriptionEditText.requestFocus();
            return;
        }

        // validate description length if provided (max 500 chars)
        if (description.length() > 500) {
            Toast.makeText(this, "Description too long (max 500 characters)", Toast.LENGTH_SHORT).show();
            binding.taskDescriptionEditText.requestFocus();
            return;
        }

        // if validation passes, proceed to update
        Task updatedTask = repository.getTaskByTaskID(taskId).getValue();
        assert updatedTask != null;
        updatedTask.setTitle(title);
        updatedTask.setDescription(description);
        updatedTask.setStatus(status);
        repository.updateTask(updatedTask);
    }

    private void deleteTask(int taskId) {
        // Logic to delete the task
        Task task = repository.getTaskByTaskID(taskId).getValue();
        repository.deleteTask(task);
        toastMaker("Task deleted successfully");
        Intent intent = MainActivity.mainActivityIntentFactory(getApplicationContext(), -1);
        startActivity(intent);
        finish();
    }

    private void populateEditTaskView(int taskId) {
        Task task = repository.getTaskByTaskID(taskId).getValue();
        assert task != null;
        binding.taskTitleEditText.setText(task.getTitle());
        binding.taskDescriptionEditText.setText(task.getDescription());
    }

    private void navigateToMainActivity() {
        UserSession currentSession = sessionManager.getCurrentSession();
        Intent mainIntent = MainActivity.mainActivityIntentFactory(this, currentSession.getUserId());
        startActivity(mainIntent);
        finish(); // Close edit task activity
    }

    private void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}