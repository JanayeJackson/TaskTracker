package com.example.elevatewebsolutions_tasktracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;

import java.util.List;

/**
 * ViewModel for managing task list data with LiveData
 * Filters tasks by current user and handles data updates
 */
public class TaskListViewModel extends AndroidViewModel {

    private final TaskManagerRepository repository;
    private final MutableLiveData<List<Task>> userTasks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private int currentUserId = -1;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        repository = TaskManagerRepository.getRepository(application);
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
     * TODO: integrate with xavier's filtering logic when ready
     */
    public void loadTasksForUser(int userId) {
        if (userId != currentUserId) {
            currentUserId = userId;
            isLoading.setValue(true);

            // basic implementation - enhance with future implementation
            // repository.getTasksForUser(userId).observe(...)

            // TODO: update this with data... just show empty list for now
            userTasks.setValue(null);
            isLoading.setValue(false);
        }
    }

    /**
     * Refresh task list for current user
     */
    public void refreshTasks() {
        if (currentUserId != -1) {
            loadTasksForUser(currentUserId);
        }
    }

    /**
     * Create new task for specified user
     * basic implementation for mvp - leaving room so xavier can enhance later
     */
    public void createTask(String title, String description, String status, int assignedUserId) {
        isLoading.setValue(true);

        // create new task object using required constructor parameters
        Task newTask = new Task(title, description, status, assignedUserId);

        // save task using repository in background thread
        new Thread(() -> {
            try {
                repository.insertTask(newTask);

                // refresh task list for current user on main thread
                if (assignedUserId == currentUserId) {
                    loadTasksForUser(currentUserId);
                }

                isLoading.postValue(false);
            } catch (Exception e) {
                android.util.Log.e("TaskListViewModel", "Error creating task: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
}
