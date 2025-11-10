package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.PostAdapter;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.FavouriteRepository;
import com.example.gallerycart.util.SessionManager;

import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavPosts;
    private PostAdapter adapter;
    private FavouriteRepository favRepo;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = findViewById(R.id.toolbarFavourites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new SessionManager(this);
        favRepo = new FavouriteRepository(this);

        rvFavPosts = findViewById(R.id.rvFavourites);
        adapter = new PostAdapter(this::onPostClicked);
        rvFavPosts.setLayoutManager(new LinearLayoutManager(this));
        rvFavPosts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = session.getUserId();
        if (userId != -1) {
            favRepo.getFavouritesByUserAsync(userId, this::updatePosts);
        }
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
