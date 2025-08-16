package com.example.elevatewebsolutions_tasktracker.Ui;

import android.app.Activity;
import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.example.elevatewebsolutions_tasktracker.MainActivity;
import com.example.elevatewebsolutions_tasktracker.R;
import com.example.elevatewebsolutions_tasktracker.SettingsActivity;
import com.example.elevatewebsolutions_tasktracker.auth.factory.AuthenticationServiceFactory;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * Verifies only admins can access SettingsActivity.
 * - Admin: Settings UI is shown.
 * - Non-admin: user is redirected to MainActivity.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityAdminGateTest {

    private Context appContext;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
        // Ensure auth/session infra is available during tests
        AuthenticationServiceFactory.getInstance().initialize(appContext);
        sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        if (sessionManager != null) {
            sessionManager.destroySession();
        }
    }

    @After
    public void tearDown() {
        if (sessionManager != null) {
            sessionManager.destroySession();
        }
    }

    /** Helper: create a session for an admin or non-admin. */
    private void createSession(boolean isAdmin) {
        User u = new User("tester", "pw", isAdmin ? "Admin" : "Member", isAdmin);
        sessionManager.createSession(u);
    }

    @Test
    public void admin_launch_showsSettingsUI() {
        createSession(true);

        try (ActivityScenario<SettingsActivity> scenario =
                     ActivityScenario.launch(SettingsActivity.class)) {
            // Adjust these IDs if your settings layout uses different ones
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.addTaskButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void nonAdmin_launch_redirectsToMain() {
        createSession(false);

        try (ActivityScenario<SettingsActivity> scenario =
                     ActivityScenario.launch(SettingsActivity.class)) {

            final Activity[] resumed = new Activity[1];
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                for (Activity a : ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED)) {
                    resumed[0] = a;
                }
            });

            assertTrue(
                    "Expected MainActivity but got: "
                            + (resumed[0] == null ? "null" : resumed[0].getClass().getName()),
                    resumed[0] instanceof MainActivity
            );
        }
    }
}
