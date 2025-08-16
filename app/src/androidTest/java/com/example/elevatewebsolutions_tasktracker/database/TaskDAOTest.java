package com.example.elevatewebsolutions_tasktracker.database;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.room.Room;

import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.LiveDataTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class TaskDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TaskManagerDatabase db;
    private TaskDAO taskDao;
    private UserDAO userDao;
    private User testUser;

    @Before
    public void setUp() throws InterruptedException {
        Log.i("TaskDAOTest", "Setting up in-memory database for testing");
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TaskManagerDatabase.class)
                .allowMainThreadQueries()
                .build();

        taskDao = db.taskDAO();
        userDao = db.userDAO();

        // create and insert test user synchronously
        testUser = new User("Test User", "password", "Test Title");
        userDao.insert(testUser);

        // get the user back with generated ID to ensure foreign key works
        List<User> users = LiveDataTestUtil.getOrAwaitValue(userDao.getAllUsers());
        if (users != null && !users.isEmpty()) {
            testUser = users.get(0); // get user with proper auto-generated ID
            Log.d("TaskDAOTest", "Test user created with ID: " + testUser.getId());
        } else {
            throw new RuntimeException("Failed to create test user");
        }
    }

    @After
    public void tearDown() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void testInsert() throws InterruptedException {
        Task task = new Task("Test Task", "This is a test task", "open", testUser.getId());
        taskDao.insert(task);

        List<Task> allTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertNotNull("task list should not be null", allTasks);
        Assert.assertEquals("should have one task", 1, allTasks.size());

        Task insertedTask = allTasks.get(0);
        Assert.assertEquals("Test Task", insertedTask.getTitle());
        Assert.assertEquals("This is a test task", insertedTask.getDescription());
        Assert.assertEquals("open", insertedTask.getStatus());
        Assert.assertEquals(testUser.getId(), insertedTask.getAssignedUserId());
    }

    @Test
    public void testUpdate() throws InterruptedException {
        Task task = new Task("Test Task", "This is a test task", "open", testUser.getId());
        taskDao.insert(task);

        // get the task with generated ID
        List<Task> tasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Task insertedTask = tasks.get(0);

        // update task
        insertedTask.setTitle("Updated Task");
        insertedTask.setDescription("Updated description");
        insertedTask.setStatus("closed");
        taskDao.update(insertedTask);

        List<Task> updatedTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Task updatedTask = updatedTasks.get(0);

        Assert.assertEquals("Updated Task", updatedTask.getTitle());
        Assert.assertEquals("Updated description", updatedTask.getDescription());
        Assert.assertEquals("closed", updatedTask.getStatus());
    }

    @Test
    public void testDelete() throws InterruptedException {
        Task task = new Task("Test Task", "This is a test task", "open", testUser.getId());
        taskDao.insert(task);

        // get the task with generated ID
        List<Task> tasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertEquals("should have one task before deletion", 1, tasks.size());

        Task taskToDelete = tasks.get(0);
        taskDao.delete(taskToDelete);

        List<Task> remainingTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertEquals("should have no tasks after deletion", 0, remainingTasks.size());
    }

    @Test
    public void testDeleteAllTasks() throws InterruptedException {
        Task task1 = new Task("Test Task 1", "This is test task 1", "open", testUser.getId());
        Task task2 = new Task("Test Task 2", "This is test task 2", "closed", testUser.getId());

        taskDao.insert(task1);
        taskDao.insert(task2);

        List<Task> tasksBeforeDelete = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertEquals("should have two tasks before delete", 2, tasksBeforeDelete.size());

        taskDao.deleteAllTasks();

        List<Task> tasksAfterDelete = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertEquals("should have no tasks after delete all", 0, tasksAfterDelete.size());
    }

    @Test
    public void testGetAllTasks() throws InterruptedException {
        Task task1 = new Task("Test Task 1", "This is test task 1", "open", testUser.getId());
        Task task2 = new Task("Test Task 2", "This is test task 2", "closed", testUser.getId());

        taskDao.insert(task1);
        taskDao.insert(task2);

        List<Task> allTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Assert.assertNotNull("tasks should not be null", allTasks);
        Assert.assertEquals("should have two tasks", 2, allTasks.size());
    }

    @Test
    public void testGetTasksByUserId() throws InterruptedException {
        Task task = new Task("User Task", "Task for user", "open", testUser.getId());
        taskDao.insert(task);

        List<Task> userTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getTasksByUserId(testUser.getId()));
        Assert.assertNotNull("user tasks should not be null", userTasks);
        Assert.assertEquals("should have one task for user", 1, userTasks.size());
        Assert.assertEquals("User Task", userTasks.get(0).getTitle());
    }

    @Test
    public void testGetTaskById() throws InterruptedException {
        Task task = new Task("Test Task", "This is a test task", "open", testUser.getId());
        taskDao.insert(task);

        // get the task with generated ID
        List<Task> tasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getAllTasks());
        Task insertedTask = tasks.get(0);

        Task retrievedTask = LiveDataTestUtil.getOrAwaitValue(taskDao.getTaskById(insertedTask.getTaskId()));
        Assert.assertNotNull("retrieved task should not be null", retrievedTask);
        Assert.assertEquals("Test Task", retrievedTask.getTitle());
        Assert.assertEquals("This is a test task", retrievedTask.getDescription());
    }

    @Test
    public void testGetTasksByStatus() throws InterruptedException {
        Task task1 = new Task("Open Task", "This is an open task", "open", testUser.getId());
        Task task2 = new Task("Closed Task", "This is a closed task", "closed", testUser.getId());

        taskDao.insert(task1);
        taskDao.insert(task2);

        List<Task> openTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getTasksByStatus("open"));
        Assert.assertNotNull("open tasks should not be null", openTasks);
        Assert.assertEquals("should have one open task", 1, openTasks.size());
        Assert.assertEquals("Open Task", openTasks.get(0).getTitle());

        List<Task> closedTasks = LiveDataTestUtil.getOrAwaitValue(taskDao.getTasksByStatus("closed"));
        Assert.assertNotNull("closed tasks should not be null", closedTasks);
        Assert.assertEquals("should have one closed task", 1, closedTasks.size());
        Assert.assertEquals("Closed Task", closedTasks.get(0).getTitle());
    }
}