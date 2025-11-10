package com.example.gallerycart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.AdminUserAdapter;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.UserRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etSearch;
    private Chip chipAll, chipArtists, chipBanned, chipRole;
    private TextView tvUserCount;
    private RecyclerView rvUsers;
    private LinearLayout emptyState;

    private AdminUserAdapter adapter;
    private UserRepository userRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        userRepository = new UserRepository(this);
        loadUsers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        chipAll = findViewById(R.id.chipAll);
        chipArtists = findViewById(R.id.chipArtists);
        chipBanned = findViewById(R.id.chipBanned);
        tvUserCount = findViewById(R.id.tvUserCount);
        rvUsers = findViewById(R.id.rvUsers);
        emptyState = findViewById(R.id.emptyState);
        chipRole = findViewById(R.id.chipRole);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        adapter.setOnUserActionListener(new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onBanUser(User user) {
                showBanConfirmationDialog(user);
            }

            @Override
            public void onDeleteUser(User user) {
                showDeleteConfirmationDialog(user);
            }
        });
    }

    private void setupListeners() {
        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateUserCount();
                updateEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            adapter.filterByType("all");
            updateUserCount();
            updateEmptyState();
        });

        chipArtists.setOnClickListener(v -> {
            currentFilter = "artists";
            adapter.filterByType("artists");
            updateUserCount();
            updateEmptyState();
        });

        chipBanned.setOnClickListener(v -> {
            currentFilter = "banned";
            adapter.filterByType("banned");
            updateUserCount();
            updateEmptyState();
        });
    }

    private void loadUsers() {
        userRepository.getAllUsers().observe(this, users -> {
            if (users != null) {
                adapter.setUsers(users);
                updateUserCount();
                updateEmptyState();
            }
        });
    }

    private void updateUserCount() {
        int count = adapter.getFilteredCount();
        String filterText = "";
        switch (currentFilter) {
            case "artists":
                filterText = " artists";
                break;
            case "banned":
                filterText = " banned users";
                break;
            default:
                filterText = " users";
                break;
        }
        tvUserCount.setText("Total: " + count + filterText);
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showBanConfirmationDialog(User user) {
        String action = user.isBanned() ? "unban" : "ban";
        String title = user.isBanned() ? "Unban User" : "Ban User";
        String message = "Are you sure you want to " + action + " " + user.getUsername() + "?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> toggleBanUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleBanUser(User user) {
        executorService.execute(() -> {
            try {
                boolean newBanStatus = !user.isBanned();
                userRepository.setBanStatus(user.getId(), newBanStatus);

                runOnUiThread(() -> {
                    String message = newBanStatus ? "User banned successfully" : "User unbanned successfully";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete " + user.getUsername() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser(User user) {
        executorService.execute(() -> {
            try {
                userRepository.getAllUsers().getValue().remove(user);

                runOnUiThread(() -> {
                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUsers();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}