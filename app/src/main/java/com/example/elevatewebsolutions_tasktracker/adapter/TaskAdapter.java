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
    private OnTaskClickListener clickListener;

    // interface for handling task item clicks
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    // constructor to set click listener
    public TaskAdapter(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    // default constructor for backward compatibility
    public TaskAdapter() {
        this.clickListener = null;
    }

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
    class TaskViewHolder extends RecyclerView.ViewHolder {
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

            // add click listener for task item interaction
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onTaskClick(task);
                    }
                }
            });

            // add visual feedback for clickable items
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }
}
