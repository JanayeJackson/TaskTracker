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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevatewebsolutions_tasktracker.adapter.TaskAdapter;
import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityMainBinding;
import com.example.elevatewebsolutions_tasktracker.viewmodel.UserViewModel;
import com.example.elevatewebsolutions_tasktracker.viewmodel.TaskListViewModel;

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
        taskAdapter = new TaskAdapter();
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
        // Inflate the menu and set visibility based on user session
        MenuItem item = menu.findItem(R.id.settingsMenuItem);
        UserSession currentSession = sessionManager.getCurrentSession();
        if(currentSession == null){
            return false; // No session, don't show menu item
        }
        item.setVisible(currentSession.isAdmin());

       item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
               Intent intent = SettingsActivity.settingsIntentFactory((getApplicationContext()));
               startActivity(intent);
               return false;
           }
       });

        return true;
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
