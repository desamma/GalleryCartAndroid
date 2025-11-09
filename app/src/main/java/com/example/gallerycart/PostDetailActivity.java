package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.CartRepository;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.util.SessionManager;
import com.google.android.material.button.MaterialButton;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvLikes;
    private TextView tvDescription;
    private TextView tvBadge;
    private MaterialButton btnDownload;
    private MaterialButton btnEdit;

    private PostRepository postRepository;
    private SessionManager sessionManager;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbarPostDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int postId = getIntent().getIntExtra(EXTRA_POST_ID, -1);
        if (postId == -1) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postRepository = new PostRepository(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadPost(postId);
    }

    private void initViews() {
        ivImage = findViewById(R.id.ivPostImageDetail);
        tvTitle = findViewById(R.id.tvPostTitleDetail);
        tvPrice = findViewById(R.id.tvPostPriceDetail);
        tvLikes = findViewById(R.id.tvPostLikesDetail);
        tvDescription = findViewById(R.id.tvPostDescriptionDetail);
        tvBadge = findViewById(R.id.tvPostBadgeDetail);
        btnDownload = findViewById(R.id.btnDownload);
        btnEdit = findViewById(R.id.btnEditPost);
    }

    private void loadPost(int postId) {
        postRepository.getPostByIdAsync(postId, post -> {
            if (post == null) {
                Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            currentPost = post;
            bindData();
        });
    }

    private void bindData() {
        tvTitle.setText(currentPost.getTitle());
        tvDescription.setText(currentPost.getDescription() != null ? currentPost.getDescription() : "");
        tvLikes.setText(currentPost.getLikeCount() + " likes");

        double price = currentPost.getPrice();
        if (price <= 0) tvPrice.setText("Free");
        else tvPrice.setText(String.valueOf(price));

        boolean isMature = currentPost.isMature();
        if (isMature) {
            tvBadge.setVisibility(TextView.VISIBLE);
            tvBadge.setText("Mature");
            ivImage.setAlpha(0.5f); // mờ 50%
        } else if (price > 0) {
            tvBadge.setVisibility(TextView.GONE);
            ivImage.setAlpha(0.8f); // mờ 20%
        } else {
            tvBadge.setVisibility(TextView.GONE);
            ivImage.setAlpha(1.0f);
        }

        String url = currentPost.getImagePath();
        if (url != null && !url.trim().isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.defaultavatar)
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.defaultavatar);
        }

        int currentUserId = sessionManager.getUserId();
        if (currentUserId != -1 && currentUserId == currentPost.getUserId()) {
            btnEdit.setVisibility(MaterialButton.VISIBLE);
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(PostDetailActivity.this, PostEditActivity.class);
                intent.putExtra(PostEditActivity.EXTRA_POST_ID, currentPost.getId());
                startActivity(intent);
            });
        } else {
            btnEdit.setVisibility(MaterialButton.GONE);
        }

        btnDownload.setOnClickListener(v -> onDownloadClicked(url, price));
    }

    private void onDownloadClicked(String url, double price) {
        if (currentPost == null) return;

        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(this, "No image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price <= 0) {
            // Free → mở trực tiếp ảnh (demo)
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            int userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    CartRepository cartRepository = new CartRepository(getApplicationContext());
                    cartRepository.addToCart(userId, currentPost.getId(), 1);
                    runOnUiThread(() ->
                            Toast.makeText(this, "Added to cart, proceed to payment.", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
