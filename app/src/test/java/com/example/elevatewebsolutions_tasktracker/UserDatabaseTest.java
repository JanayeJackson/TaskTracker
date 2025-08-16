package com.example.elevatewebsolutions_tasktracker;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;
import com.example.elevatewebsolutions_tasktracker.database.UserDAO;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * database test for user insert and delete operations
 * tests room database functionality with user entity
 */
@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {

    // rule to execute livedata synchronously in tests
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TaskManagerDatabase database;
    private UserDAO userDao;

    @Before
    public void setUp() {
        // create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                TaskManagerDatabase.class
        )
        .allowMainThreadQueries() // allow queries on main thread for testing
        .build();

        userDao = database.userDAO();
    }

    @After
    public void tearDown() {
        database.close();
    }

    /**
     * test basic user insertion
     * verifies user can be inserted and retrieved
     */
    @Test
    public void insertUser_shouldAddUserToDatabase() throws InterruptedException {
        // create test user
        User testUser = new User("testuser", "testpassword", "Test User");

        // insert user
        userDao.insert(testUser);

        // verify user was inserted by checking all users
        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);

        assertNotNull("user list should not be null", userList);
        assertEquals("should have exactly one user", 1, userList.size());

        User insertedUser = userList.get(0);
        assertEquals("username should match", "testuser", insertedUser.getUsername());
        assertEquals("password should match", "testpassword", insertedUser.getPassword());
        assertEquals("title should match", "Test User", insertedUser.getTitle());
        assertFalse("should not be admin by default", insertedUser.getAdmin());
        assertTrue("should have valid timestamp", insertedUser.getCreatedTimestamp() > 0);
    }

    /**
     * test user insertion with admin role
     */
    @Test
    public void insertAdminUser_shouldSetAdminFlag() throws InterruptedException {
        User adminUser = new User("admin", "adminpass", "Administrator", true);

        userDao.insert(adminUser);

        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);

        assertEquals("should have one user", 1, userList.size());

        User insertedAdmin = userList.get(0);
        assertTrue("should be admin", insertedAdmin.getAdmin());
        assertEquals("username should match", "admin", insertedAdmin.getUsername());
    }

    /**
     * test multiple user insertion
     */
    @Test
    public void insertMultipleUsers_shouldAddAllUsers() throws InterruptedException {
        User user1 = new User("user1", "pass1", "User One");
        User user2 = new User("user2", "pass2", "User Two");
        User admin = new User("admin", "adminpass", "Admin User", true);

        // insert multiple users
        userDao.insert(user1, user2, admin);

        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);

        assertEquals("should have three users", 3, userList.size());

        // verify users are ordered by username (as per DAO query)
        assertEquals("first user should be admin", "admin", userList.get(0).getUsername());
        assertEquals("second user should be user1", "user1", userList.get(1).getUsername());
        assertEquals("third user should be user2", "user2", userList.get(2).getUsername());
    }

    /**
     * test user deletion
     * verifies user can be removed from database
     */
    @Test
    public void deleteUser_shouldRemoveUserFromDatabase() throws InterruptedException {
        // insert user first
        User testUser = new User("deletetest", "password", "Delete Test");
        userDao.insert(testUser);

        // verify insertion
        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have one user before deletion", 1, userList.size());

        // delete the user
        User userToDelete = userList.get(0); // get user with generated ID
        userDao.delete(userToDelete);

        // verify deletion
        List<User> usersAfterDeletion = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have no users after deletion", 0, usersAfterDeletion.size());
    }

    /**
     * test deleting specific user from multiple users
     */
    @Test
    public void deleteSpecificUser_shouldOnlyRemoveTargetUser() throws InterruptedException {
        User user1 = new User("keeper", "pass1", "Keep This User");
        User user2 = new User("deleteme", "pass2", "Delete This User");

        userDao.insert(user1, user2);

        // verify both users exist
        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have two users", 2, userList.size());

        // find and delete specific user
        User userToDelete = null;
        for (User user : userList) {
            if ("deleteme".equals(user.getUsername())) {
                userToDelete = user;
                break;
            }
        }
        assertNotNull("should find user to delete", userToDelete);

        userDao.delete(userToDelete);

        // verify only one user remains
        List<User> remainingUsers = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have one user remaining", 1, remainingUsers.size());
        assertEquals("remaining user should be keeper", "keeper", remainingUsers.get(0).getUsername());
    }

    /**
     * test getting user by username
     */
    @Test
    public void getUserByUsername_shouldReturnCorrectUser() throws InterruptedException {
        User testUser = new User("findme", "password", "Find Me");
        userDao.insert(testUser);

        LiveData<User> foundUser = userDao.getUserByUsername("findme");
        User result = LiveDataTestUtil.getOrAwaitValue(foundUser);

        assertNotNull("should find user", result);
        assertEquals("username should match", "findme", result.getUsername());
        assertEquals("title should match", "Find Me", result.getTitle());
    }

    /**
     * test getting user by username that doesn't exist
     */
    @Test
    public void getUserByUsername_nonExistentUser_shouldReturnNull() throws InterruptedException {
        LiveData<User> foundUser = userDao.getUserByUsername("nonexistent");
        User result = LiveDataTestUtil.getOrAwaitValue(foundUser);

        assertNull("should not find non-existent user", result);
    }

    /**
     * test synchronous user lookup for authentication
     */
    @Test
    public void getUserByUsernameSync_shouldReturnUserForAuth() {
        User testUser = new User("synctest", "password", "Sync Test");
        userDao.insert(testUser);

        // test synchronous method used for authentication
        User result = userDao.getUserByUsernameSync("synctest");

        assertNotNull("should find user synchronously", result);
        assertEquals("username should match", "synctest", result.getUsername());
        assertEquals("password should match", "password", result.getPassword());
    }

    /**
     * test delete all users functionality
     */
    @Test
    public void deleteAll_shouldRemoveAllUsers() throws InterruptedException {
        // insert multiple users
        User user1 = new User("user1", "pass1", "User 1");
        User user2 = new User("user2", "pass2", "User 2");
        User user3 = new User("user3", "pass3", "User 3");

        userDao.insert(user1, user2, user3);

        // verify users exist
        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have three users", 3, userList.size());

        // delete all users
        userDao.deleteAll();

        // verify all users deleted
        List<User> remainingUsers = LiveDataTestUtil.getOrAwaitValue(allUsers);
        assertEquals("should have no users after delete all", 0, remainingUsers.size());
    }

    /**
     * test user id assignment after insertion
     */
    @Test
    public void insertUser_shouldAssignAutoGeneratedId() throws InterruptedException {
        User testUser = new User("idtest", "password", "ID Test");

        // id should be 0 before insertion (default)
        assertEquals("id should be 0 before insertion", 0, testUser.getId());

        userDao.insert(testUser);

        // retrieve user to check generated id
        LiveData<List<User>> allUsers = userDao.getAllUsers();
        List<User> userList = LiveDataTestUtil.getOrAwaitValue(allUsers);

        User insertedUser = userList.get(0);
        assertTrue("id should be generated (> 0)", insertedUser.getId() > 0);
    }
}
