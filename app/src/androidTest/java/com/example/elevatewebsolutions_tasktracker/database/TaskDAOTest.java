package com.example.elevatewebsolutions_tasktracker.database;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.room.Room;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDAOTest {

    private static TaskManagerDatabase db;
    private static TaskDAO taskDao;
    private static UserDAO userDAO;
    private static User user;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    @BeforeClass
    public static void setUp() {
        Log.i("TaskDAOTest", "Setting up in-memory database for testing");
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

    @AfterClass
    public static void tearDown(){
        db.close();
    }

    @Test
    public void testInsert() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            Task retrievedTask = taskDao.getTaskById(task.getTaskId()).getValue();
            Assert.assertNotNull(retrievedTask);
            Assert.assertEquals("Test Task", retrievedTask.getTitle());
            Assert.assertEquals("This is a test task", retrievedTask.getDescription());
            taskDao.deleteAllTasks();
        });
    }

    @Test
    public void testUpdate() {
        //TODO: Implement testUpdate
    }

    @Test
    public void testDelete() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            Task retrievedTask = taskDao.getTaskById(task.getTaskId()).getValue();
            Assert.assertNotNull(retrievedTask);
            taskDao.delete(retrievedTask);
            Assert.assertNull(taskDao.getTaskById(retrievedTask.getTaskId()));
            taskDao.deleteAllTasks();
        });
    }

    @Test
    public void testDeleteAllTasks() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        Task task1 = new Task("Test Task 1", "This is another test task", "open", user.getId());
        Task task2 = new Task("Test Task 2", "This is yet another test task", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            taskDao.insert(task1);
            taskDao.insert(task2);
            Assert.assertEquals(3, Objects.requireNonNull(taskDao.getAllTasks().getValue()).size());
            taskDao.deleteAllTasks();
            Assert.assertNull(taskDao.getAllTasks().getValue());
        });
    }

    @Test
    public void testGetAllTasks() {
        //TODO: Implement testGetAllTasks

    }

}