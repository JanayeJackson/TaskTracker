package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevatewebsolutions_tasktracker.adapter.TaskAdapter;
import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityMainBinding;
import com.example.elevatewebsolutions_tasktracker.viewmodel.UserViewModel;
import com.example.elevatewebsolutions_tasktracker.viewmodel.TaskListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TASK_MANAGER";

    // ViewModel for authentication state management
    private UserViewModel userViewModel;
    private TaskListViewModel taskListViewModel;

    // RecyclerView components
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;

    // Keep existing SessionManager for backwards compatibility during transition
    private SessionManager sessionManager;
    private TextView usernameDisplayTextView;
    private Button logoutButton;

    private ActivityMainBinding binding;

    public static Intent mainActivityIntentFactory(Context applicationContext, int userId) {
        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.putExtra("USER_ID", userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar as the action bar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("TaskTracker");
        }

        // Initialize repository early to ensure database and users are created
        TaskManagerRepository repository = TaskManagerRepository.getRepository(getApplication());

        // Initialize UserViewModel for authentication state management
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize session manager (keeping for backwards compatibility during transition)
        sessionManager = new SessionManager(this);

        // Set up LiveData observers for authentication state
        setupAuthenticationObservers();

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // User not logged in, redirect to login
            Intent loginIntent = LoginActivity.loginIntentFactory(this);
            startActivity(loginIntent);
            finish(); // Close MainActivity so user can't go back
            return;
        }

        // Initialize UI components
        initializeViews();
        setupLogoutButton();
        setupAddTaskButton();
        setupFloatingActionButton();
        displayCurrentUser();

        // Log current user info for debugging
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            android.util.Log.i(TAG, "User logged in: " + currentSession.getUsername() +
                    " (Admin: " + currentSession.isAdmin() + ")");
        }

        // Initialize RecyclerView for task list
        initializeTaskList();
    }

    private void initializeViews() {
        usernameDisplayTextView = findViewById(R.id.usernameDisplayTextView);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void initializeTaskList() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        // create adapter with click listener for task interaction
        taskAdapter = new TaskAdapter(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                handleTaskClick(task);
            }
        });

        tasksRecyclerView.setAdapter(taskAdapter);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize TaskListViewModel
        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        // setup task list observers
        setupTaskListObservers();

        // load tasks for current user if logged in
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            taskListViewModel.loadTasksForUser(currentSession.getUserId());
        }
    }

    /**
     * handle task item clicks from recyclerview
     * shows task details and prepares for future navigation to edittask
     */
    private void handleTaskClick(Task task) {
        // for now, show task details in a toast
        // when isaiah completes edittaskactivity, we can navigate there
        String message = "Task: " + task.getTitle() + "\n" +
                "Status: " + task.getStatus() + "\n" +
                "Tap to edit (coming soon)";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // TODO: navigate to EditTaskActivity when Isaiah completes it
        // Intent editIntent = EditTaskActivity.editTaskActivityIntentFactory(this, task.getTaskId());
        // startActivity(editIntent);
    }

    /**
     * Setup LiveData observers for task list updates
     * Connects ViewModel data changes to RecyclerView updates
     */
    private void setupTaskListObservers() {
        // observe task list changes and update adapter
        taskListViewModel.getUserTasks().observe(this, tasks -> {
            android.util.Log.d(TAG, "Task list updated: " +
                    (tasks != null ? tasks.size() + " tasks" : "null"));
            taskAdapter.updateTasks(tasks);
        });

        // observe loading state (for future progress indicator)
        taskListViewModel.getIsLoading().observe(this, isLoading -> {
            android.util.Log.d(TAG, "Task loading state: " + isLoading);
            // TODO: finish loading state
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // show/hide menu items based on user role
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession == null) {
            return false; // no session, don't show menu
        }

        // settings only visible to admin users
        MenuItem settingsItem = menu.findItem(R.id.settingsMenuItem);
        if (settingsItem != null) {
            settingsItem.setVisible(currentSession.isAdmin());
        }

        // profile and logout visible to all users (no changes needed)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settingsMenuItem) {
            // launch settings activity - admin only
            //Intent intent = SettingsActivity.settingsIntentFactory(this, sessionManager.getCurrentUserId());
            //startActivity(intent);
            return true;

        } else if (itemId == R.id.profileMenuItem) {
            // show user profile info - available to all users
            UserSession session = sessionManager.getCurrentSession();
            if (session != null) {
                String message = "User: " + session.getUsername() +
                        (session.isAdmin() ? " (Admin)" : " (User)");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            return true;

        } else if (itemId == R.id.logoutMenuItem) {
            // logout - available to all users
            performLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }

    /**
     * setup add task button click listener
     * launches addtaskactivity when admin clicks the button
     */
    private void setupAddTaskButton() {
        // only setup if button actually exists (admin users)
        if (binding.addTaskButton != null) {
            binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch addtaskactivity using intent factory
                    Intent addTaskIntent = AddTaskActivity.addTaskActivityIntentFactory(MainActivity.this);
                    startActivity(addTaskIntent);
                }
            });
        }
    }

    /**
     * setup floating action button for adding tasks
     * modern material design approach - available to all users
     */
    private void setupFloatingActionButton() {
        FloatingActionButton addTaskFab = findViewById(R.id.addTaskFab);
        if (addTaskFab != null) {
            addTaskFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch addtaskactivity - available to all users now
                    Intent addTaskIntent = AddTaskActivity.addTaskActivityIntentFactory(MainActivity.this);
                    startActivity(addTaskIntent);
                }
            });
        }
    }

    private void displayCurrentUser() {
        UserSession currentSession = sessionManager.getCurrentSession();
        if (currentSession != null) {
            String displayText = "Logged in as: " + currentSession.getUsername();
            if (currentSession.isAdmin()) {
                displayText += " (Admin)";
                // Show admin-specific UI elements
                binding.userHeader.setVisibility(View.VISIBLE);
                binding.user1.setVisibility(View.VISIBLE);
                binding.addTaskButton.setVisibility(View.VISIBLE);
            }
            usernameDisplayTextView.setText(displayText);
            // Update options menu to reflect current user
            invalidateOptionsMenu();
        } else {
            usernameDisplayTextView.setText("Not logged in");
        }
    }

    private void performLogout() {
        // Log the logout action
        UserSession currentSession = userViewModel.getCurrentUser().getValue();
        if (currentSession != null) {
            android.util.Log.i(TAG, "User logging out: " + currentSession.getUsername());
        }

        // Use ViewModel logout method instead of direct SessionManager call
        userViewModel.logout();


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check session validity when activity resumes
        if (!sessionManager.isLoggedIn()) {
            // Session expired, redirect to login
            Intent loginIntent = LoginActivity.loginIntentFactory(this);
            startActivity(loginIntent);
            finish();
        }
    }


    private void setupAuthenticationObservers() {
        userViewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            android.util.Log.d(TAG, "ViewModel login state changed: " + isLoggedIn);

            if (!isLoggedIn) {
                Intent loginIntent = LoginActivity.loginIntentFactory(this);
                startActivity(loginIntent);
                finish();
            }
            // Note: handle login success in the currentUser observer
        });

        userViewModel.getCurrentUser().observe(this, userSession -> {
            android.util.Log.d(TAG, "ViewModel user session changed: " +
                    (userSession != null ? userSession.getUsername() : "null"));

            if (userSession != null) {
                updateUIForUser(userSession);
            } else {
                clearUserUI();
            }
        });

        userViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                android.util.Log.w(TAG, "ViewModel authentication error: " + errorMessage);
                // TODO: Show error to user via Toast or Snackbar????
            }
        });
    }

    /**
     * Update UI elements based on current user session from ViewModel
     */
    private void updateUIForUser(UserSession userSession) {
        String displayText = "Logged in as: " + userSession.getUsername();
        if (userSession.isAdmin()) {
            displayText += " (Admin)";
            // Show admin-specific UI elements
            if (binding.userHeader != null) binding.userHeader.setVisibility(View.VISIBLE);
            if (binding.user1 != null) binding.user1.setVisibility(View.VISIBLE);
            if (binding.addTaskButton != null) binding.addTaskButton.setVisibility(View.VISIBLE);
        }
        usernameDisplayTextView.setText(displayText);
        invalidateOptionsMenu();
    }

    /**
     * Clear user-specific UI elements when no session is available
     */
    private void clearUserUI() {
        usernameDisplayTextView.setText("Not logged in");
        // Hide admin-specific UI elements
        if (binding.userHeader != null) binding.userHeader.setVisibility(View.GONE);
        if (binding.user1 != null) binding.user1.setVisibility(View.GONE);
        if (binding.addTaskButton != null) binding.addTaskButton.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clean up any resources if needed...
    }
}
