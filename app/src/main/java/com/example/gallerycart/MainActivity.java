package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.PostAdapter;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvUsername;
    private MaterialButton btnLogout;
    private SessionManager sessionManager;

    private RecyclerView rvFeaturedPosts;
    private RecyclerView rvRandomPosts;
    private PostAdapter featuredAdapter;
    private PostAdapter randomAdapter;
    private PostRepository postRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        postRepository = new PostRepository(this);

        initViews();
        setupRecyclerViews();
        loadUserData();
        setupListeners();
        loadPostSections();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_all_artists) {
            Intent intent = new Intent(this, AllArtistsActivity.class);
            startActivity(intent);
            return true;
        }
        // Sau này nếu thêm action_home / create_post thì handle ở đây
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);

        rvFeaturedPosts = findViewById(R.id.rvFeaturedPosts);
        rvRandomPosts = findViewById(R.id.rvRandomPosts);
    }

    private void setupRecyclerViews() {
        featuredAdapter = new PostAdapter(this::onPostClicked);
        randomAdapter = new PostAdapter(this::onPostClicked);

        rvFeaturedPosts.setLayoutManager(new LinearLayoutManager(this));
        rvFeaturedPosts.setAdapter(featuredAdapter);
        rvFeaturedPosts.setNestedScrollingEnabled(false);

        rvRandomPosts.setLayoutManager(new LinearLayoutManager(this));
        rvRandomPosts.setAdapter(randomAdapter);
        rvRandomPosts.setNestedScrollingEnabled(false);
    }

    private void loadUserData() {
        String username = sessionManager.getUsername();
        tvUsername.setText(username != null ? username : "User");
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadPostSections() {
        // 5 bài nổi bật (like nhiều nhất)
        postRepository.getTopLikedPostsAsync(5, this::updateFeaturedPosts);

        // 5 bài ngẫu nhiên
        postRepository.getRandomPostsAsync(5, this::updateRandomPosts);
    }

    private void updateFeaturedPosts(List<Post> posts) {
        featuredAdapter.setPosts(posts);
    }

    private void updateRandomPosts(List<Post> posts) {
        randomAdapter.setPosts(posts);
    }

    private void onPostClicked(Post post) {
        // Tạm thời chỉ show Toast.
        // Sau này sẽ chuyển sang màn chi tiết Post / View all art.
        Toast.makeText(this, "Post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logout();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
