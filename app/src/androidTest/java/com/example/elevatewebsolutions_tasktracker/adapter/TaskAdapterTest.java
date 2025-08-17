package com.example.elevatewebsolutions_tasktracker.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.elevatewebsolutions_tasktracker.R;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/**
 * Unit tests for {@code TaskAdapter}.
 *
 * 
 */
@RunWith(AndroidJUnit4.class)
public class TaskAdapterTest {

    private Context context;
    private TaskAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        adapter = TaskAdapter.create();
    }

    @Test
    public void bind_bindsTitleDescriptionAndStatus() {
        Task t = new Task("Title A", "Desc A", "To Do", 1);
        adapter.updateTasks(Arrays.asList(t));

        FrameLayout parent = new FrameLayout(context);
        TaskAdapter.TaskViewHolder vh = adapter.onCreateViewHolder(parent, 0);
        adapter.onBindViewHolder(vh, 0);

        TextView title = vh.itemView.findViewById(R.id.taskTitleTextView);
        TextView desc = vh.itemView.findViewById(R.id.taskDescriptionTextView);
        TextView status = vh.itemView.findViewById(R.id.taskStatusTextView);

        assertNotNull(title);
        assertNotNull(desc);
        assertNotNull(status);

        assertEquals("Title A", title.getText().toString());
        assertEquals("Desc A", desc.getText().toString());
        // If your row shows only the raw status, change expected to "To Do".
        assertEquals("Status: To Do", status.getText().toString());
    }
}
