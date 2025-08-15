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

import java.util.ArrayList;
import java.util.List;
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

    @After
    public void clearDatabase() {
        databaseWriteExecutor.execute(() -> {
            taskDao.deleteAllTasks();
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
        });
    }

    @Test
    public void testUpdate() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        Task updatedTask = task;
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            updatedTask.setTitle("Test Task Updated");
            taskDao.update(updatedTask);
            Task retrievedTask = taskDao.getTaskById(task.getTaskId()).getValue();
            Assert.assertNotNull(retrievedTask);
            Assert.assertNotEquals("Test Task", retrievedTask.getTitle());
            Assert.assertEquals("Test Task Updated", retrievedTask.getTitle());
            Assert.assertEquals("This is a test task", retrievedTask.getDescription());
        });
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
        Task task = new Task("Test Task 0", "This is a test task", "open", user.getId());
        Task task1 = new Task("Test Task 1", "This is another test task", "open", user.getId());
        Task task2 = new Task("Test Task 2", "This is yet another test task", "open", user.getId());
        List<Task> tasks = new ArrayList<>();
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            taskDao.insert(task1);
            taskDao.insert(task2);
            Assert.assertEquals(3, Objects.requireNonNull(taskDao.getAllTasks().getValue()).size());
            Assert.assertNotNull(taskDao.getAllTasks().getValue());
            tasks.addAll(Objects.requireNonNull(taskDao.getAllTasks().getValue()));
            for(int i = 0; i < tasks.size(); i++) {
                Assert.assertEquals("Test Task " + i, tasks.get(i).getTitle());
            }
        });

    }

    @Test
    public void testGetTasksByUserId() {
        Task task = new Task("User Task", "Task for user", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            List<Task> tasks = Objects.requireNonNull(taskDao.getTasksByUserId(user.getId()).getValue());
            Assert.assertNotNull(tasks);
            Assert.assertEquals(1, tasks.size());
            Assert.assertEquals("User Task", tasks.get(0).getTitle());
        });
    }
    @Test
    public void testGetTaskById() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            Task retrievedTask = taskDao.getTaskById(task.getTaskId()).getValue();
            Assert.assertNotNull(retrievedTask);
            Assert.assertEquals("Test Task", retrievedTask.getTitle());
            Assert.assertEquals("This is a test task", retrievedTask.getDescription());
        });
    }

    @Test
    public void testGetTasksByStatus() {
        Task task = new Task("Test Task", "This is a test task", "open", user.getId());
        Task task1 = new Task("Test Task 1", "This is another test task", "closed", user.getId());
        databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
            taskDao.insert(task1);
            List<Task> openTasks = Objects.requireNonNull(taskDao.getTasksByStatus("open").getValue());
            Assert.assertNotNull(openTasks);
            Assert.assertEquals(1, openTasks.size());
            Assert.assertEquals("Test Task", openTasks.get(0).getTitle());
        });
    }

}