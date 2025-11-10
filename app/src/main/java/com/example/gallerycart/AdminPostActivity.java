package com.example.gallerycart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.AdminPostAdapter;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.repository.UserRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etSearch;
    private Chip chipAll, chipPortfolio, chipMature;
    private Spinner spinnerSort;
    private TextView tvPostCount;
    private RecyclerView rvPosts;
    private LinearLayout emptyState;

    private AdminPostAdapter adapter;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private String currentFilter = "all";
    private Map<Integer, String> userCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        postRepository = new PostRepository(this);
        userRepository = new UserRepository(this);

        loadPosts();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        chipAll = findViewById(R.id.chipAll);
        chipPortfolio = findViewById(R.id.chipPortfolio);
        chipMature = findViewById(R.id.chipMature);
        spinnerSort = findViewById(R.id.spinnerSort);
        tvPostCount = findViewById(R.id.tvPostCount);
        rvPosts = findViewById(R.id.rvPosts);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new AdminPostAdapter(this);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(adapter);

        adapter.setOnPostActionListener(new AdminPostAdapter.OnPostActionListener() {
            @Override
            public void onViewPost(Post post) {
                showPostDetailsDialog(post);
            }

            @Override
            public void onDeletePost(Post post) {
                showDeleteConfirmationDialog(post);
            }

            @Override
            public String getArtistName(int userId) {
                if (userCache.containsKey(userId)) {
                    return userCache.get(userId);
                }
                executorService.execute(() -> {
                    User user = userRepository.getUserById(userId);
                    if (user != null) {
                        userCache.put(userId, user.getUsername());
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                });
                return "Loading...";
            }
        });
    }

    private void setupListeners() {
        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updatePostCount();
                updateEmptyState();
            }
        });

        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            adapter.filterByType("all");
            updatePostCount();
            updateEmptyState();
        });

        chipPortfolio.setOnClickListener(v -> {
            currentFilter = "portfolio";
            adapter.filterByType("portfolio");
            updatePostCount();
            updateEmptyState();
        });

        chipMature.setOnClickListener(v -> {
            currentFilter = "mature";
            adapter.filterByType("mature");
            updatePostCount();
            updateEmptyState();
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sortOptions = {"date_desc", "date_asc", "likes_desc", "sales_desc", "price_desc", "price_asc"};
                if (position < sortOptions.length) {
                    adapter.sortBy(sortOptions[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadPosts() {
        postRepository.getAllPostsAsync(posts -> runOnUiThread(() -> {
            if (posts != null) {
                adapter.setPosts(posts);
                updatePostCount();
                updateEmptyState();
            }
        }));
    }

    private void updatePostCount() {
        int count = adapter.getFilteredCount();
        String filterText;
        switch (currentFilter) {
            case "portfolio":
                filterText = " portfolio posts";
                break;
            case "mature":
                filterText = " mature posts";
                break;
            default:
                filterText = " posts";
                break;
        }
        tvPostCount.setText("Total: " + count + filterText);
    }


    private void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvPosts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showPostDetailsDialog(Post post) {
        String artistName = userCache.getOrDefault(post.getUserId(), "Unknown Artist");
        String details = "🖼 Title: " + post.getTitle() + "\n\n" +
                "👨‍🎨 Artist: " + artistName + "\n" +
                "💰 Price: " + post.getPrice() + " VND\n" +
                "❤️ Likes: " + post.getLikeCount() + "\n" +
                "🛒 Sales: " + post.getSaleCount() + "\n" +
                "📁 Portfolio: " + (post.isPortfolio() ? "Yes" : "No") + "\n" +
                "🔞 Mature: " + (post.isMature() ? "Yes" : "No") + "\n\n" +
                "📝 Description:\n" + (post.getDescription() != null ? post.getDescription() : "No description");

        new AlertDialog.Builder(this)
                .setTitle("Post Details")
                .setMessage(details)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showDeleteConfirmationDialog(Post post) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete \"" + post.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executorService.execute(() -> {
                        postRepository.deletePost(post.getId());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                            loadPosts();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
