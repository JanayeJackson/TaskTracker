package com.example.elevatewebsolutions_tasktracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevatewebsolutions_tasktracker.R;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic RecyclerView adapter for task list
 * TODO: replace with Isaiah's full implementation from issue #9
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // update task list and refresh UI
    public void updateTasks(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        // TODO: basic refreshing... isiah can optimize this
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for individual task items
     * TODO: enhance with isaiah's selection handling
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView statusTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.taskDescriptionTextView);
            statusTextView = itemView.findViewById(R.id.taskStatusTextView);
        }

        public void bind(Task task) {
            titleTextView.setText(task.getTitle());
            descriptionTextView.setText(task.getDescription());
            statusTextView.setText("Status: " + task.getStatus());

            // TODO: add click listener for navigation to EditTaskActivity
        }
    }
}
