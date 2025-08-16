package com.example.elevatewebsolutions_tasktracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.TaskDAO;
import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;
import com.example.elevatewebsolutions_tasktracker.database.UserDAO;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * RecyclerView navigation tests.
 *
 * <p>Verifies that tapping a list item sends an intent to {@link EditTaskActivity}
 * and includes the expected extra. Kept simple to match the level of other tests:
 * JUnit 4, Espresso, Espresso-Intents, Room DAO seeding.</p>
 */
@RunWith(AndroidJUnit4.class)
public final class RecyclerViewNavigationTest {

    private static final String TAG = "RecyclerViewNavTest";
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final String EXTRA_TASK_ID = "Task_Id";

    private ActivityScenario<MainActivity> scenario;
    private TaskManagerDatabase db;
    private TaskDAO taskDao;
    private UserDAO userDao;
    private SessionManager sessionManager;

    @Before
    public void setUp() throws Exception {
        Log.i(TAG, "setUp: seed DB, create session, stub EditTaskActivity, launch MainActivity");

        Context context = ApplicationProvider.getApplicationContext();
        db = TaskManagerDatabase.getDatabase(context);
        taskDao = db.taskDAO();
        userDao = db.userDAO();
        sessionManager = new SessionManager(context);

        // Seed a test user and two tasks so the list is populated
        seedUserAndTasks();

        // Create a logged-in session so MainActivity does not redirect to LoginActivity
        User u = getUserSync("uitest");
        if (u == null) {
            u = getUserSync("user");
        }
        Assert.assertNotNull("A user must exist for session", u);
        sessionManager.createSession(u);

        // Initialize Intents and stub the destination so the activity does not actually start
        Intents.init();
        Intents.intending(IntentMatchers.hasComponent(EditTaskActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(0, new Intent()));

        // Launch the screen under test
        scenario = ActivityScenario.launch(MainActivity.class);
        Assert.assertNotNull("ActivityScenario should not be null", scenario);

        // Ensure the list is visible before interacting
        onView(withId(R.id.tasksRecyclerView)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        Log.i(TAG, "tearDown: close scenario, clear tasks, release Intents, destroy session");
        if (scenario != null) {
            scenario.close();
        }
        // Keep tests deterministic
        Future<?> clear = TaskManagerDatabase.databaseWriteExecutor.submit(() -> taskDao.deleteAllTasks());
        clear.get();

        sessionManager.destroySession();
        Intents.release();
    }

    @Test
    public void clickFirstItem_sendsIntentToEditTaskActivity() {
        onView(withId(R.id.tasksRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(FIRST, click()));

        Intents.intended(IntentMatchers.hasComponent(EditTaskActivity.class.getName()));
    }

    @Test
    public void clickSecondItem_includesTaskIdExtra() {
        onView(withId(R.id.tasksRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(SECOND, click()));

        Intents.intended(AllOf.allOf(
                IntentMatchers.hasComponent(EditTaskActivity.class.getName()),
                IntentMatchers.hasExtraWithKey(EXTRA_TASK_ID)
        ));
    }

    private void seedUserAndTasks() throws ExecutionException, InterruptedException {
        Future<?> f = TaskManagerDatabase.databaseWriteExecutor.submit(() -> {
            // Ensure a known user
            User u = userDao.getUserByUsernameSync("uitest");
            if (u == null) {
                u = new User("uitest", "pass", "UI Tester");
                userDao.insert(u);
            }
            User user = userDao.getUserByUsernameSync("uitest");

            // Start clean
            taskDao.deleteAllTasks();

            // Insert two tasks for this user
            taskDao.insert(new Task("UI Test Task 1", "from test", "To Do", user.getId()));
            taskDao.insert(new Task("UI Test Task 2", "from test", "In Progress", user.getId()));
        });
        f.get();
    }

    private User getUserSync(String username) throws ExecutionException, InterruptedException {
        Future<User> f = TaskManagerDatabase.databaseWriteExecutor.submit(
                () -> userDao.getUserByUsernameSync(username));
        return f.get();
    }
}
