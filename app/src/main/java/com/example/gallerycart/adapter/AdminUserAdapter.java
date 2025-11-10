package com.example.gallerycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.R;
import com.example.gallerycart.data.entity.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private List<User> users = new ArrayList<>();
    private List<User> usersFiltered = new ArrayList<>();
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onBanUser(User user);
        void onDeleteUser(User user);
    }

    public AdminUserAdapter(Context context) {
        this.context = context;
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        this.usersFiltered = new ArrayList<>(this.users);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        usersFiltered.clear();
        if (query == null || query.isEmpty()) {
            usersFiltered.addAll(users);
        } else {
            String lowerQuery = query.toLowerCase();
            for (User user : users) {
                if (user.getUsername().toLowerCase().contains(lowerQuery) ||
                        user.getEmail().toLowerCase().contains(lowerQuery)) {
                    usersFiltered.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        usersFiltered.clear();
        switch (type) {
            case "all":
                usersFiltered.addAll(users);
                break;
            case "artists":
                for (User user : users) {
                    if (user.isArtist()) {
                        usersFiltered.add(user);
                    }
                }
                break;
            case "banned":
                for (User user : users) {
                    if (user.isBanned()) {
                        usersFiltered.add(user);
                    }
                }
                break;
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return usersFiltered.size();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersFiltered.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return usersFiltered.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAvatar;
        private final TextView tvUsername;
        private final TextView tvEmail;
        private final Chip chipRole;
        private final TextView tvArtistBadge;
        private final TextView tvBannedBadge;
        private final TextView tvEmailVerified;
        private final MaterialButton btnBan;
        private final MaterialButton btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            chipRole = itemView.findViewById(R.id.chipRole);
            tvArtistBadge = itemView.findViewById(R.id.tvArtistBadge);
            tvBannedBadge = itemView.findViewById(R.id.tvBannedBadge);
            tvEmailVerified = itemView.findViewById(R.id.tvEmailVerified);
            btnBan = itemView.findViewById(R.id.btnBan);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            String role = user.getRole();
            if (role != null) {
                chipRole.setText(role.substring(0, 1).toUpperCase() + role.substring(1));
            } else {
                chipRole.setText("Customer");
            }

            tvArtistBadge.setVisibility(user.isArtist() ? View.VISIBLE : View.GONE);
            tvBannedBadge.setVisibility(user.isBanned() ? View.VISIBLE : View.GONE);
            tvEmailVerified.setVisibility(user.isEmailConfirmed() ? View.VISIBLE : View.GONE);

            btnBan.setText(user.isBanned() ? "Unban" : "Ban");
            btnBan.setTextColor(context.getResources().getColor(
                    user.isBanned() ? R.color.success_color : R.color.warning_color));
            btnBan.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    context.getResources().getColor(
                            user.isBanned() ? R.color.success_color : R.color.warning_color)));

            boolean isAdmin = "admin".equals(user.getRole());
            btnBan.setEnabled(!isAdmin);
            btnDelete.setEnabled(!isAdmin);

            btnBan.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBanUser(user);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteUser(user);
                }
            });
        }
    }
}