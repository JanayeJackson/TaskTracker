package com.example.elevatewebsolutions_tasktracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevatewebsolutions_tasktracker.R;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RecyclerView adapter for displaying {@link Task} items.
 *
 * <p>Uses the ViewHolder pattern and a small DiffUtil pass inside
 * {@link #updateTasks(List)} so UI updates are efficient when the list changes.
 */
public final class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    /**
     * Click listener for task rows.
     */
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    private final List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener clickListener;

    public TaskAdapter() {
        setHasStableIds(true);
    }

    /**
     * Constructor with click listener
     * @param onTaskClickListener Listener for task click events
     */
    public TaskAdapter(OnTaskClickListener onTaskClickListener) {
        this();
        this.clickListener = onTaskClickListener;
    }

    /**
     * Sets the click listener to receive callbacks when a row is tapped.
     */
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * Replace the current list with {@code newTasks} and dispatch only the minimal set of
     * notify* calls using DiffUtil.
     */
    public void updateTasks(List<Task> newTasks) {
        if (newTasks == null) {
            newTasks = new ArrayList<>();
        }
        final List<Task> old = new ArrayList<>(this.tasks);

        List<Task> finalNewTasks = newTasks;
        DiffUtil.DiffResult diff =
                DiffUtil.calculateDiff(
                        new DiffUtil.Callback() {
                            @Override
                            public int getOldListSize() {
                                return old.size();
                            }

                            @Override
                            public int getNewListSize() {
                                return finalNewTasks.size();
                            }

                            @Override
                            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                return old.get(oldItemPosition).getTaskId()
                                        == finalNewTasks.get(newItemPosition).getTaskId();
                            }

                            @Override
                            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                Task a = old.get(oldItemPosition);
                                Task b = finalNewTasks.get(newItemPosition);
                                return Objects.equals(a.getTitle(), b.getTitle())
                                        && Objects.equals(a.getDescription(), b.getDescription())
                                        && Objects.equals(a.getStatus(), b.getStatus())
                                        && a.getAssignedUserId() == b.getAssignedUserId();
                            }
                        },
                        true);

        this.tasks.clear();
        this.tasks.addAll(newTasks);

        diff.dispatchUpdatesTo(
                new ListUpdateCallback() {
                    @Override
                    public void onInserted(int position, int count) {
                        notifyItemRangeInserted(position, count);
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        notifyItemRangeRemoved(position, count);
                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        notifyItemMoved(fromPosition, toPosition);
                    }

                    @Override
                    public void onChanged(int position, int count, Object payload) {
                        notifyItemRangeChanged(position, count, payload);
                    }
                });
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getTaskId();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Factory method for easy adapter creation
     */
    public static TaskAdapter create() {
        return new TaskAdapter();
    }

    /**
     * Factory method for adapter creation with click listener
     */
    public static TaskAdapter create(OnTaskClickListener listener) {
        return new TaskAdapter(listener);
    }

    /**
     * ViewHolder that caches references to row views.
     */
    final class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView statusTextView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.taskDescriptionTextView);
            statusTextView = itemView.findViewById(R.id.taskStatusTextView);
        }

        void bind(Task task) {
            titleTextView.setText(task.getTitle());
            descriptionTextView.setText(task.getDescription());
            statusTextView.setText("Status: " + task.getStatus());

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTaskClick(task);
                }
            });

            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }
}