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
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    /**
     * Interface for handling user click events.
     */
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final List<User> userList = new ArrayList<>();
    private OnUserClickListener listener;


    public UserAdapter() {
        setHasStableIds(true);
    }

    /**
     * Constructor with click listener
     * @param listener Listener for user click events
     */
    public UserAdapter(OnUserClickListener listener) {
        this();
        this.listener = listener;
    }

    /**
     * Sets the click listener for user items.
     */
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    /**
     * Replace the current user list with a new list and notify the adapter.
     */
    public void updateUserList(List<User> newUserList) {
        if (newUserList == null) {
            newUserList = new ArrayList<>();
        }

        final List<User> old = new ArrayList<>(this.userList);

        List<User> oldUserList = newUserList;
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(
                    new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return old.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return oldUserList.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return old.get(oldItemPosition).getId()
                                == oldUserList.get(newItemPosition).getId();
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        User a = old.get(oldItemPosition);
                        User b = oldUserList.get(newItemPosition);
                        return Objects.equals(a.getId(), b.getId())
                                && Objects.equals(a.getPassword(), b.getPassword())
                                && Objects.equals(a.getTitle(), b.getTitle())
                                && a.getAdmin() == b.getAdmin();
                    }
                },
                true);
        this.userList.clear();
        this.userList.addAll(newUserList);

        // Notify the adapter of the changes
        diffResult.dispatchUpdatesTo(
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
        return userList.get(position).getId();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Factory method for easy adapter creation
     */
    public static UserAdapter create() {
        return new UserAdapter();
    }

    /**
     * Factory method for adapter creation with click listener
     */
    public static UserAdapter create(OnUserClickListener listener) {
        return new UserAdapter(listener);
    }

    /**
     * ViewHolder that caches references to row views.
     */
    final class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView titleTextView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            titleTextView = itemView.findViewById(R.id.userTitleTextView);
        }

        void bind(User user) {
            usernameTextView.setText(user.getUsername());
            titleTextView.setText(user.getTitle());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });

            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }

}
