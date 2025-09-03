package com.example.elevatewebsolutions_tasktracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
                //navigateToMainActivity();
            }
        }else{
            toastMaker("No task ID provided");
            //navigateToMainActivity();
        }

        repository = TaskManagerRepository.getRepository(getApplication());

        setupStatusSpinner();
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

        binding.deleteButton.setOnClickListener(v -> {
            repository.deleteTaskById(taskId);
        });
    }

    private void updateTask(int taskId) {
        // get form values
        String title = binding.taskTitleEditText.getText().toString().trim();
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
        repository.getTaskByTaskID(taskId).observe(this, t -> {
            t.setTitle(title);
            t.setDescription(description);
            t.setStatus(status);
            repository.updateTask(t);

            toastMaker("Task updated successfully");
            navigateToMainActivity();
        });
    }

    private void deleteTask(int taskId) {

        repository.getTaskByTaskID(taskId).observe(this, t -> {
            //Display a confirmation dialog before deleting the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditTaskActivity.this);
            final AlertDialog alertDialog = alertBuilder.create();

            alertBuilder.setMessage("Delete task: " + t.getTitle() + "?");

            alertBuilder.setPositiveButton("Delete?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Delete the user with the given userId
                    repository.deleteTask(t);
                    toastMaker("Task deleted successfully");
                    navigateToMainActivity();
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

    private void populateEditTaskView(int taskId) {

        repository.getTaskByTaskID(taskId).observe(this, t -> {
            Log.d("DEBUG", "Observed task: " + t);
            binding.taskTitleEditText.setText(t.getTitle());
            binding.taskDescriptionEditText.setText(t.getDescription());
            binding.taskStatusSpinner.setSelection(t.getStatus().equals("To Do") ? 0 :
            t.getStatus().equals("In Progress") ? 1 : 2);
        });
    }

    /**
     * setup status dropdown with basic options
     */
    private void setupStatusSpinner() {
        // basic task statuses for mvp
        String[] statuses = {"To Do", "In Progress", "Complete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.taskStatusSpinner.setAdapter(adapter);

        // default to "To Do" for new tasks
        binding.taskStatusSpinner.setSelection(0);
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

    public static Intent editTaskActivityIntentFactory(Context context, int taskId) {
        Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra("Task_Id", taskId);
        return intent;
    }
}