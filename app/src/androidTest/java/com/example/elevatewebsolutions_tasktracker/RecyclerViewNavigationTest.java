package com.example.elevatewebsolutions_tasktracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI navigation tests for the task list RecyclerView.
 *
 * <p>Verifies that tapping a list item launches {@link MainActivity} and that the expected
 * UI elements are displayed. Mirrors the style used in {@code TaskDAOTest}: explicit setup and
 * teardown, logging, and straightforward assertions.
 */
@RunWith(AndroidJUnit4.class)
public final class RecyclerViewNavigationTest {

    private static final String TAG = "RecyclerViewNavigationTest";
    private static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final String EXTRA_TASK_ID = "TASK_ID";

    private ActivityScenario<MainActivity> scenario;

    @BeforeClass
    public static void beforeAll() {
        Log.i(TAG, "Starting RecyclerView navigation test suite");
    }

    @AfterClass
    public static void afterAll() {
        Log.i(TAG, "Finished RecyclerView navigation test suite");
    }

    @Before
    public void setUp() {
        Log.i(TAG, "setUp: initializing Intents and launching MainActivity");
        Intents.init();
        scenario = ActivityScenario.launch(MainActivity.class);
        Assert.assertNotNull("ActivityScenario should not be null", scenario);
    }

    @After
    public void tearDown() {
        Log.i(TAG, "tearDown: closing ActivityScenario and releasing Intents");
        if (scenario != null) {
            scenario.close();
        }
        Intents.release();
    }

    @Test
    public void recyclerItemClick_navigatesToDetailActivity() {
        // Verify list is visible
        onView(withId(R.id.tasksRecyclerView)).check(matches(isDisplayed()));

        // Click first item
        onView(withId(R.id.tasksRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(FIRST_POSITION, click()));

        // Verify navigation to MainActivity (detail mode)
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));

        // Verify main container is still visible after click
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void recyclerItemClick_carriesTaskIdExtra() {
        // Verify list is visible
        onView(withId(R.id.tasksRecyclerView)).check(matches(isDisplayed()));

        // Click second item
        onView(withId(R.id.tasksRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(SECOND_POSITION, click()));

        // Verify MainActivity launched with the expected extra key
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtraWithKey(EXTRA_TASK_ID));
    }
}