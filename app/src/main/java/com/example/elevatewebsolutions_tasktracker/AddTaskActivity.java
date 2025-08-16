package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.viewmodel.TaskListViewModel;
import com.example.elevatewebsolutions_tasktracker.viewmodel.UserViewModel;

/**
 * activity for creating new tasks
 * simple form with title, description, status
 */
public class AddTaskActivity extends AppCompatActivity {

    // form components
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner statusSpinner;
    private Button saveButton;
    private Button cancelButton;

    // viewmodels for data management
    private TaskListViewModel taskListViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    /**
     * intent factory pattern - creates intent for launching this activity
     */
    public static Intent addTaskActivityIntentFactory(Context context) {
        return new Intent(context, AddTaskActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // check if user is logged in
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // redirect to login if not authenticated
            finish();
            return;
        }

        // initialize viewmodels
        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // setup form components
        initializeViews();
        setupStatusSpinner();
        setupClickListeners();
    }

    /**
     * find and initialize all form components
     */
    private void initializeViews() {
        titleEditText = findViewById(R.id.taskTitleEditText);
        descriptionEditText = findViewById(R.id.taskDescriptionEditText);
        statusSpinner = findViewById(R.id.taskStatusSpinner);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
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
        statusSpinner.setAdapter(adapter);

        // default to "To Do" for new tasks
        statusSpinner.setSelection(0);
    }

    /**
     * setup button click listeners
     */
    private void setupClickListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate and save task
                attemptSaveTask();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // just close activity without saving
                finish();
            }
        });
    }

    /**
     * validate form and save task if valid
     * checks title is not empty and within length limits
     */
    private void attemptSaveTask() {
        // get form values
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();

        // validate title (required field)
        if (title.isEmpty()) {
            Toast.makeText(this, "Task title is required", Toast.LENGTH_SHORT).show();
            titleEditText.requestFocus();
            return;
        }

        // validate title length (max 100 chars)
        if (title.length() > 100) {
            Toast.makeText(this, "Title too long (max 100 characters)", Toast.LENGTH_SHORT).show();
            titleEditText.requestFocus();
            return;
        }

        // validate description length if provided (max 500 chars)
        if (description.length() > 500) {
            Toast.makeText(this, "Description too long (max 500 characters)", Toast.LENGTH_SHORT).show();
            descriptionEditText.requestFocus();
            return;
        }

        // if validation passes, proceed to save
        saveTaskToDatabase(title, description, status);
    }

    /**
     * save task to database using viewmodel
     * gets current user id and creates task assigned to them
     */
    private void saveTaskToDatabase(String title, String description, String status) {
        // get current user id from session
        int currentUserId = sessionManager.getCurrentUserId();
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // create task via viewmodel
        taskListViewModel.createTask(title, description, status, currentUserId);

        // observe loading state to show feedback
        taskListViewModel.getIsLoading().observe(this, isLoading -> {
            if (!isLoading) {
                // task saved successfully
                Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();

                // close activity and return to main screen
                finish();
            }
        });

        // show feedback to user
        Toast.makeText(this, "Saving task...", Toast.LENGTH_SHORT).show();
    }
}
