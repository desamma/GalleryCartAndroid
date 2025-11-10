package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.PostAdapter;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.PostRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllPostsActivity extends AppCompatActivity {

    private RecyclerView rvAllPosts;
    private PostAdapter adapter;
    private PostRepository postRepository;

    private EditText etSearch;
    private MaterialButton btnSort;

    private final List<Post> allPosts = new ArrayList<>();
    private boolean sortAscending = true; // true = A-Z, false = Z-A

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);

        Toolbar toolbar = findViewById(R.id.toolbarAllPosts);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        postRepository = new PostRepository(this);

        rvAllPosts = findViewById(R.id.rvAllPosts);
        adapter = new PostAdapter(this::onPostClicked);
        rvAllPosts.setLayoutManager(new LinearLayoutManager(this));
        rvAllPosts.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearchPostTitle);
        btnSort = findViewById(R.id.btnSortTitle);

        setupSearchAndSort();

        loadPosts();
    }

    private void setupSearchAndSort() {
        // Text change → filter + sort
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilterAndSort();
            }
        });

        // Click sort → toggle A-Z / Z-A
        btnSort.setOnClickListener(v -> {
            sortAscending = !sortAscending;
            updateSortButtonText();
            applyFilterAndSort();
        });

        updateSortButtonText();
    }

    private void updateSortButtonText() {
        btnSort.setText(sortAscending ? "Sort A-Z" : "Sort Z-A");
    }

    private void loadPosts() {
        postRepository.getAllPostsAsync(this::updatePosts);
    }

    private void updatePosts(List<Post> posts) {
        allPosts.clear();
        if (posts != null) {
            allPosts.addAll(posts);
        }
        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        String query = etSearch.getText() != null
                ? etSearch.getText().toString().trim().toLowerCase()
                : "";

        List<Post> filtered = new ArrayList<>();
        for (Post p : allPosts) {
            String title = p.getTitle() != null ? p.getTitle() : "";
            if (query.isEmpty() || title.toLowerCase().contains(query)) {
                filtered.add(p);
            }
        }

        // Sort theo title A-Z hoặc Z-A
        Collections.sort(filtered, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                String t1 = o1.getTitle() != null ? o1.getTitle() : "";
                String t2 = o2.getTitle() != null ? o2.getTitle() : "";
                return t1.compareToIgnoreCase(t2);
            }
        });
        if (!sortAscending) {
            Collections.reverse(filtered);
        }

        adapter.setPosts(filtered);
    }

    private void onPostClicked(Post post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
