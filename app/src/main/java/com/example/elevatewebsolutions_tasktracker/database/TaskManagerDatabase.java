package com.example.elevatewebsolutions_tasktracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
// import com.example.elevatewebsolutions_tasktracker.database.entities.Comment;

// TODO: Add Comment entity when implemented
@Database(entities = {User.class, Task.class}, version = 1, exportSchema = false)
public abstract class TaskManagerDatabase extends RoomDatabase {

    // Table names
    public static final String USER_TABLE = "user_table";
    public static final String TASK_TABLE = "task_table";
    public static final String COMMENT_TABLE = "comment_table";

    // Database name
    private static final String DATABASE_NAME = "TaskManager_database";

    // Singleton instance
    private static volatile TaskManagerDatabase INSTANCE;

    // Abstract methods to get DAOs
    public abstract UserDAO userDAO();
    public abstract TaskDAO taskDAO();
    // TODO: Add CommentDAO when Comment entity is implemented
    // public abstract CommentDAO commentDAO();

    /**
     * Singleton pattern implementation for database access
     * @param context Application context
     * @return TaskManagerDatabase instance
     */
    public static TaskManagerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskManagerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TaskManagerDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
