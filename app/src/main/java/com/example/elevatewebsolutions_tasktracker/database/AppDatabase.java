package com.example.elevatewebsolutions_tasktracker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.elevatewebsolutions_tasktracker.database.TaskDAO;
import com.example.elevatewebsolutions_tasktracker.database.UserDAO;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

@Database(
        entities = {User.class, Task.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "App_database";

    // Define DAOs
    public abstract UserDAO userDao();
    public abstract TaskDAO taskDao();
}
