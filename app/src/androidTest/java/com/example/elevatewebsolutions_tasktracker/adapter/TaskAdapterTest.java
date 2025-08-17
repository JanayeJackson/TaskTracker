package com.example.elevatewebsolutions_tasktracker.adapter;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@code TaskAdapter}.
 *
 * <p>Skeleton with runner and setup.</p>
 */
@RunWith(AndroidJUnit4.class)
public class TaskAdapterTest {

    private Context context;
    private TaskAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        adapter = TaskAdapter.create(); // factory without click listener
    }
}
