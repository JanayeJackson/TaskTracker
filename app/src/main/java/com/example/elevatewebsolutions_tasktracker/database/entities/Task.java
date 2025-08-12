package com.example.elevatewebsolutions_tasktracker.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;

import java.util.Objects;

@Entity(tableName = TaskManagerDatabase.TASK_TABLE,
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "assignedUserId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "assignedUserId")})
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private String title;
    private String description;
    private String status;
    private int assignedUserId;

    public Task(String title, String description, String status, int assignedUserId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedUserId = assignedUserId;
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(int assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId &&
                assignedUserId == task.assignedUserId &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, title, description, status, assignedUserId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", assignedUserId=" + assignedUserId +
                '}';
    }
}
