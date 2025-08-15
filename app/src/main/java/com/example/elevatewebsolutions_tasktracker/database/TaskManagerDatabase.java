package com.example.elevatewebsolutions_tasktracker.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.elevatewebsolutions_tasktracker.MainActivity;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Admin Account:
//Username: admin
//Password: admin123

//Regular User Account:
//Username: user
//Password: user123



// TODO: Add Comment entity when implemented
@Database(entities = {User.class, Task.class}, version = 4, exportSchema = false)
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

    //Number of threads repository will run on
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Migration from version 1 to 2 (adds authentication fields)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add new columns for authentication
            database.execSQL("ALTER TABLE " + USER_TABLE + " ADD COLUMN passwordSalt TEXT");
            database.execSQL("ALTER TABLE " + USER_TABLE + " ADD COLUMN createdTimestamp INTEGER NOT NULL DEFAULT 0");

            // Update existing records with current timestamp
            database.execSQL("UPDATE " + USER_TABLE + " SET createdTimestamp = " + System.currentTimeMillis());
        }
    };

    // Migration from version 3 to 4 (adds index on assignedUserId)
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add index on assignedUserId for better foreign key performance
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_task_table_assignedUserId` ON `" + TASK_TABLE + "` (`assignedUserId`)");
        }
    };

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
                            .addMigrations(MIGRATION_1_2, MIGRATION_3_4)
                            .fallbackToDestructiveMigration() // Allow destructive migration for version changes
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Adds Default values to the database in order to test functionality
     * Adds an admin, adds a user, and adds a couple of tasks
     */
    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
       @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i(MainActivity.TAG, "Database CREATED!");
            createDefaultUsersSync();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.i(MainActivity.TAG, "Database OPENED!");
            // Ensure default users exist every time database opens
            createDefaultUsersSync();
        }

        private void createDefaultUsersSync() {
            // Execute synchronously to ensure users are created before any queries
            try {
                databaseWriteExecutor.execute(() -> {
                    UserDAO dao = INSTANCE.userDAO();

                    // Check if admin user exists, if not create it
                    User existingAdmin = dao.getUserByUsernameSync("admin");
                    if (existingAdmin == null) {
                        User admin = new User("admin", "admin123", "Administrator", true);
                        dao.insert(admin);
                        Log.i(MainActivity.TAG, "Created admin user: admin/admin123");
                    }

                    // Check if regular user exists, if not create it
                    User existingUser = dao.getUserByUsernameSync("user");
                    if (existingUser == null) {
                        User testuser = new User("user", "user123", "Regular User");
                        dao.insert(testuser);
                        Log.i(MainActivity.TAG, "Created regular user: user/user123");
                    }

                    Log.i(MainActivity.TAG, "Default users verified/created:");
                    Log.i(MainActivity.TAG, "Admin - username: admin, password: admin123");
                    Log.i(MainActivity.TAG, "User - username: user, password: user123");
                });

            } catch (Exception e) {
                Log.e(MainActivity.TAG, "Error creating default users", e);
            }
        }
    };
}
