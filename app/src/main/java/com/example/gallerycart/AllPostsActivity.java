package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.PostAdapter;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.PostRepository;

import java.util.List;

public class AllPostsActivity extends AppCompatActivity {

    private RecyclerView rvAllPosts;
    private PostAdapter adapter;
    private PostRepository postRepository;

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

        loadPosts();
    }

    private void loadPosts() {
        postRepository.getAllPostsAsync(this::updatePosts);
    }

    private void updatePosts(List<Post> posts) {
        adapter.setPosts(posts);
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
