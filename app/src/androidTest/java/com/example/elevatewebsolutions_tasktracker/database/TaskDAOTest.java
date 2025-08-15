package com.example.elevatewebsolutions_tasktracker.database;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.room.Room;

import com.example.elevatewebsolutions_tasktracker.MainActivity;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDAOTest extends TestCase {

    private TaskManagerDatabase db;
    private TaskDAO taskDao;
    private UserDAO userDAO;
    private User user;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public void setUp() throws Exception {
        super.setUp();
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TaskManagerDatabase.class)
                .allowMainThreadQueries()
                .build();

            databaseWriteExecutor.execute(() -> {
                taskDao = db.taskDAO();
                userDAO = db.userDAO();
                user = new User("Test User", "password", "user");
                userDAO.insert(user);// Clear existing users
            });
    }

    public void tearDown() throws Exception {
        super.tearDown();
        db.close();
    }

    public void testInsert() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            Task retrievedTask = taskDao.getTaskById(task.getTaskId()).getValue();
            assertNotNull(retrievedTask);
            assertEquals("Test Task", retrievedTask.getTitle());
            assertEquals("This is a test task", retrievedTask.getDescription());
        });

    }

    public void testUpdate() {
        //TODO: Implement testUpdate
    }

    public void testDelete() {
        //TODO: Implement testDelete
    }

    public void testDeleteAllTasks() {
        //TODO: Implement testDeleteAllTasks
    }

    public void testGetAllTasks() {
        //TODO: Implement testGetAllTasks

    }

}