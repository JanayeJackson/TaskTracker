package com.example.elevatewebsolutions_tasktracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing task list data with LiveData
 * Filters tasks by current user and handles data updates
 */
public class TaskListViewModel extends AndroidViewModel {

    private final TaskManagerRepository repository;
    private final MutableLiveData<Integer> currentUserId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // search and filter state
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> statusFilter = new MutableLiveData<>("All");

    // raw user tasks from database
    private final LiveData<List<Task>> rawUserTasks;

    // filtered tasks based on search and status
    private final LiveData<List<Task>> filteredTasks;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        repository = TaskManagerRepository.getRepository(application);

        // get raw tasks from database when user changes
        rawUserTasks = Transformations.switchMap(currentUserId, userId -> {
            if (userId != null && userId != -1) {
                return repository.getAllTasksByUserId(userId);
            } else {
                MutableLiveData<List<Task>> emptyTasks = new MutableLiveData<>();
                emptyTasks.setValue(new ArrayList<>());
                return emptyTasks;
            }
        });

        // combine all filter triggers - updates when any filter changes or raw data changes
        MutableLiveData<String> combinedTrigger = new MutableLiveData<>();

        filteredTasks = Transformations.switchMap(combinedTrigger, trigger -> {
            MutableLiveData<List<Task>> result = new MutableLiveData<>();
            List<Task> tasks = rawUserTasks.getValue();
            List<Task> filtered = applyFilters(tasks, searchQuery.getValue(), statusFilter.getValue());
            result.setValue(filtered);
            return result;
        });

        // trigger filtering when raw tasks, search, or status change
        rawUserTasks.observeForever(tasks -> combinedTrigger.setValue("raw_" + System.currentTimeMillis()));
        searchQuery.observeForever(query -> combinedTrigger.setValue("search_" + query));
        statusFilter.observeForever(status -> combinedTrigger.setValue("status_" + status));
    }

    // getters for UI observation
    public LiveData<List<Task>> getUserTasks() {
        return filteredTasks;
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

    /**
     * Update search query and refresh filtered results
     */
    public void setSearchQuery(String query) {
        String cleanQuery = query != null ? query.trim() : "";
        if (!cleanQuery.equals(searchQuery.getValue())) {
            searchQuery.setValue(cleanQuery);
            refreshFilteredTasks();
        }
    }

    /**
     * Update status filter and refresh results
     */
    public void setStatusFilter(String status) {
        String cleanStatus = status != null ? status : "All";
        if (!cleanStatus.equals(statusFilter.getValue())) {
            statusFilter.setValue(cleanStatus);
            refreshFilteredTasks();
        }
    }

    /**
     * Manually refresh filtered tasks when filters change
     */
    private void refreshFilteredTasks() {
        List<Task> rawTasks = rawUserTasks.getValue();
        if (rawTasks != null) {
            List<Task> filtered = applyFilters(rawTasks, searchQuery.getValue(), statusFilter.getValue());
            // trigger update by setting value on filtered tasks observer
            MutableLiveData<List<Task>> temp = new MutableLiveData<>();
            temp.setValue(filtered);
        }
    }

    /**
     * Apply search and status filters to task list
     */
    private List<Task> applyFilters(List<Task> tasks, String query, String status) {
        if (tasks == null) return new ArrayList<>();

        List<Task> filtered = new ArrayList<>();
        String lowerQuery = query != null ? query.toLowerCase().trim() : "";

        for (Task task : tasks) {
            // apply status filter first
            if (!"All".equals(status) && !status.equals(task.getStatus())) {
                continue;
            }

            // then apply search filter
            if (!lowerQuery.isEmpty()) {
                String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                String taskStatus = task.getStatus() != null ? task.getStatus().toLowerCase() : "";

                // search in title, description, or status
                if (!title.contains(lowerQuery) &&
                    !description.contains(lowerQuery) &&
                    !taskStatus.contains(lowerQuery)) {
                    continue;
                }
            }

            filtered.add(task);
        }

        return filtered;
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
