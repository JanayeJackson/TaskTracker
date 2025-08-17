package com.example.elevatewebsolutions_tasktracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.elevatewebsolutions_tasktracker.database.entities.Comment;

import java.util.List;

@Dao
public interface CommentDAO {

    @Insert
    void insert(Comment... comments);

    @Update
    void update(Comment... comments);

    @Delete
    void delete(Comment comment);

    @Query("SELECT * FROM " + TaskManagerDatabase.COMMENT_TABLE + " WHERE commentId = :commentId")
    Comment getCommentByCommentId(int commentId);

    @Query("SELECT * FROM " + TaskManagerDatabase.COMMENT_TABLE + " WHERE taskId = :taskId ORDER BY timestamp ASC")
    LiveData<List<Comment>> getCommentsByTaskId(int taskId);

    @Query("SELECT * FROM " + TaskManagerDatabase.COMMENT_TABLE + " WHERE authorId = :authorId ORDER BY timestamp DESC")
    LiveData<List<Comment>> getCommentsByAuthor(int authorId);

    @Query("SELECT * FROM " + TaskManagerDatabase.COMMENT_TABLE + " ORDER BY timestamp DESC")
    LiveData<List<Comment>> getAllComments();

    @Query("DELETE FROM " + TaskManagerDatabase.COMMENT_TABLE + " WHERE taskId = :taskId")
    void deleteCommentsByTaskId(int taskId);

    @Query("DELETE FROM " + TaskManagerDatabase.COMMENT_TABLE + " WHERE commentId = :commentId")
    void deleteCommentById(int commentId);
}
