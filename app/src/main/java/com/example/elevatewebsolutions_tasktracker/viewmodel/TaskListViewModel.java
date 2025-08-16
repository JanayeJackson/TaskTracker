package com.example.elevatewebsolutions_tasktracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;

import java.util.List;

/**
 * ViewModel for managing task list data with LiveData
 * Filters tasks by current user and handles data updates
 */
public class TaskListViewModel extends AndroidViewModel {

    private final TaskManagerRepository repository;
    private final MutableLiveData<Integer> currentUserId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // LiveData that transforms based on current user ID
    private final LiveData<List<Task>> userTasks;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        repository = TaskManagerRepository.getRepository(application);

        // Set up transformation that automatically updates when currentUserId changes
        userTasks = Transformations.switchMap(currentUserId, userId -> {
            if (userId != null && userId != -1) {
                return repository.getAllTasksByUserId(userId);
            } else {
                // Return empty LiveData for invalid user IDs
                MutableLiveData<List<Task>> emptyTasks = new MutableLiveData<>();
                emptyTasks.setValue(null);
                return emptyTasks;
            }
        });
    }

    // getters for UI observation
    public LiveData<List<Task>> getUserTasks() {
        return userTasks;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Load tasks for specific user ID
     * Now properly connects to database via repository
     */
    public void loadTasksForUser(int userId) {
        if (userId != getCurrentUserIdValue()) {
            isLoading.setValue(true);
            currentUserId.setValue(userId);
            // The Transformations.switchMap above will handle the actual data loading
            isLoading.setValue(false);
        }
    }

    private int getCurrentUserIdValue() {
        Integer userId = currentUserId.getValue();
        return userId != null ? userId : -1;
    }

    /**
     * Refresh task list for current user
     */
    public void refreshTasks() {
        Integer userId = currentUserId.getValue();
        if (userId != null && userId != -1) {
            // Force refresh by setting the same user ID again
            currentUserId.setValue(userId);
        }
    }

    /**
     * Create new task for specified user
     * Enhanced implementation that properly refreshes the task list
     */
    public void createTask(String title, String description, String status, int assignedUserId) {
        isLoading.setValue(true);

        // create new task object using required constructor parameters
        Task newTask = new Task(title, description, status, assignedUserId);

        // save task using repository in background thread
        new Thread(() -> {
            try {
                repository.insertTask(newTask);

                // refresh task list on main thread after successful insert
                // The LiveData will automatically update due to database changes
                isLoading.postValue(false);

            } catch (Exception e) {
                android.util.Log.e("TaskListViewModel", "Error creating task: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
}
