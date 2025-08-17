package com.example.elevatewebsolutions_tasktracker.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.elevatewebsolutions_tasktracker.R;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit tests for {@link TaskAdapter}.
 *
 * <p>Verifies that data is correctly bound to the ViewHolder views and that
 * row clicks notify the registered listener.
 */
@RunWith(AndroidJUnit4.class)
public class TaskAdapterTest {

    private Context context;
    private TaskAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // Use the factory method to create adapter without click listener
        adapter = TaskAdapter.create();
    }

    @Test
    public void bind_bindsTitleDescriptionAndStatus() {
        // Arrange
        Task t = new Task("Title A", "Desc A", "To Do", /*userId=*/ 1);
        adapter.updateTasks(Collections.singletonList(t));

        // Create holder and bind position 0
        FrameLayout parent = new FrameLayout(context);
        RecyclerView.ViewHolder vh = adapter.onCreateViewHolder(parent, 0);

        // Use unchecked cast since we know the actual type
        @SuppressWarnings("unchecked")
        TaskAdapter.TaskViewHolder taskVh = (TaskAdapter.TaskViewHolder) vh;
        adapter.onBindViewHolder(taskVh, 0);

        // Assert bound text
        TextView title = vh.itemView.findViewById(R.id.taskTitleTextView);
        TextView desc = vh.itemView.findViewById(R.id.taskDescriptionTextView);
        TextView status = vh.itemView.findViewById(R.id.taskStatusTextView);

        assertNotNull(title);
        assertNotNull(desc);
        assertNotNull(status);

        assertEquals("Title A", title.getText().toString());
        assertEquals("Desc A", desc.getText().toString());
        assertEquals("Status: To Do", status.getText().toString());
    }

    @Test
    public void click_notifiesListenerWithCorrectTask() {
        Task t1 = new Task("Title 1", "Desc 1", "In Progress", 1);

        // Create adapter with click listener using the atomic reference
        AtomicReference<Task> clicked = new AtomicReference<>(null);
        TaskAdapter.OnTaskClickListener listener = clicked::set;
        adapter = TaskAdapter.create(listener);

        adapter.updateTasks(Collections.singletonList(t1));

        FrameLayout parent = new FrameLayout(context);
        RecyclerView.ViewHolder vh = adapter.onCreateViewHolder(parent, 0);

        // Use unchecked cast since we know the actual type
        @SuppressWarnings("unchecked")
        TaskAdapter.TaskViewHolder taskVh = (TaskAdapter.TaskViewHolder) vh;
        adapter.onBindViewHolder(taskVh, 0);

        vh.itemView.performClick();

        assertNotNull("Click listener should be invoked", clicked.get());
        assertEquals("Title 1", clicked.get().getTitle());
    }
}