package com.example.elevatewebsolutions_tasktracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task... tasks);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM " + TaskManagerDatabase.TASK_TABLE)
    void deleteAllTasks();

    @Query("SELECT * FROM " + TaskManagerDatabase.TASK_TABLE + " ORDER BY taskId ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM " + TaskManagerDatabase.TASK_TABLE + " WHERE assignedUserId = :userId ORDER BY taskId ASC")
    LiveData<List<Task>> getTasksByUserId(int userId);

    @Query("SELECT * FROM " + TaskManagerDatabase.TASK_TABLE + " WHERE taskId = :taskId")
    LiveData<Task> getTaskById(int taskId);

    @Query("SELECT * FROM " + TaskManagerDatabase.TASK_TABLE + " WHERE status = :status ORDER BY taskId ASC")
    LiveData<List<Task>> getTasksByStatus(String status);

    @Query("SELECT * FROM " + TaskManagerDatabase.TASK_TABLE + " WHERE title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY taskId ASC")
    LiveData<List<Task>> searchTasks(String searchQuery);
}
