package com.example.elevatewebsolutions_tasktracker.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;

import java.util.Objects;

@Entity(tableName = TaskManagerDatabase.COMMENT_TABLE,
        foreignKeys = {
                @ForeignKey(entity = Task.class,
                        parentColumns = "taskId",
                        childColumns = "taskId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "authorId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "taskId"), @Index(value = "authorId")})
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private int commentId;
    private int taskId;
    private int authorId;
    private String text;
    private long timestamp;

    public Comment(int taskId, int authorId, String text) {
        this.taskId = taskId;
        this.authorId = authorId;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId == comment.commentId &&
               taskId == comment.taskId &&
               authorId == comment.authorId &&
               timestamp == comment.timestamp &&
               Objects.equals(text, comment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, taskId, authorId, text, timestamp);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", taskId=" + taskId +
                ", authorId=" + authorId +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
