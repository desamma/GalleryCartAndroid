package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerycart.adapter.CommentAdapter;
import com.example.gallerycart.data.entity.Comment;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.repository.CommentRepository;
import com.example.gallerycart.repository.FavouriteRepository;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvLikes;
    private TextView tvDescription;
    private TextView tvBadge;
    private MaterialButton btnAddToCart;
    private MaterialButton btnEdit;
    private MaterialButton btnLike;

    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private MaterialButton btnSendComment;

    private PostRepository postRepository;
    private FavouriteRepository favouriteRepository;
    private CommentRepository commentRepository;
    private SessionManager sessionManager;
    private Post currentPost;
    private CartViewModel cartViewModel;

    private boolean isFavourited = false;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbarPostDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int postId = getIntent().getIntExtra(EXTRA_POST_ID, -1);
        if (postId == -1) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postRepository = new PostRepository(this);
        favouriteRepository = new FavouriteRepository(this);
        commentRepository = new CommentRepository(this);
        sessionManager = new SessionManager(this);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews();
        loadPost(postId);
        observeViewModel();
    }

    private void initViews() {
        ivImage = findViewById(R.id.ivPostImageDetail);
        tvTitle = findViewById(R.id.tvPostTitleDetail);
        tvPrice = findViewById(R.id.tvPostPriceDetail);
        tvLikes = findViewById(R.id.tvPostLikesDetail);
        tvDescription = findViewById(R.id.tvPostDescriptionDetail);
        tvBadge = findViewById(R.id.tvPostBadgeDetail);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnEdit = findViewById(R.id.btnEditPost);
        btnLike = findViewById(R.id.btnLike);

        rvComments = findViewById(R.id.rvComments);
        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);

        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
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

            // trạng thái favourite
            int uid = sessionManager.getUserId();
            if (uid != -1) {
                favouriteRepository.isFavouritedAsync(currentPost.getId(), uid, fav -> runOnUiThread(() -> {
                    isFavourited = fav;
                    updateLikeButton();
                }));
            }

            // load comments
            commentRepository.getCommentsByPostAsync(currentPost.getId(), comments -> runOnUiThread(() -> {
                commentAdapter.setItems(toDisplay(comments));
            }));
        });
    }

    private void observeViewModel() {
        cartViewModel.addToCartResult.observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** KHÔNG truy vấn DB để lấy username trong UI thread nữa. */
    private List<CommentAdapter.CommentDisplay> toDisplay(List<Comment> comments) {
        List<CommentAdapter.CommentDisplay> list = new ArrayList<>();
        if (comments == null) return list;
        for (Comment c : comments) {
            String username = "User#" + c.getUserId();
            String dateStr = "";
            if (c.getCommentDate() != null) {
                try { dateStr = sdf.format(c.getCommentDate()); } catch (Exception ignored) {}
            }
            String header = username + " · " + dateStr;
            list.add(new CommentAdapter.CommentDisplay(header, c.getContent() == null ? "" : c.getContent()));
        }
        return list;
    }

    private void bindData() {
        tvTitle.setText(currentPost.getTitle());
        tvDescription.setText(currentPost.getDescription() != null ? currentPost.getDescription() : "");
        tvLikes.setText(currentPost.getLikeCount() + " likes");

        double price = currentPost.getPrice();
        if (price <= 0) {
            tvPrice.setText("Free");
            btnAddToCart.setText("Download");
        } else {
            tvPrice.setText(String.valueOf(price));
            btnAddToCart.setText("Add to Cart");
        }

        boolean isMature = currentPost.isMature();
        if (isMature) {
            tvBadge.setVisibility(TextView.VISIBLE);
            tvBadge.setText("Mature");
            ivImage.setAlpha(0.5f);
        } else if (price > 0) {
            tvBadge.setVisibility(TextView.GONE);
            ivImage.setAlpha(0.8f);
        } else {
            tvBadge.setVisibility(TextView.GONE);
            ivImage.setAlpha(1.0f);
        }

        String url = currentPost.getImagePath();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).placeholder(R.drawable.post_placeholder).into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.post_placeholder);
        }

        int currentUserId = sessionManager.getUserId();
        boolean isOwner = (currentUserId != -1 && currentUserId == currentPost.getUserId());

        if (isOwner) {
            btnAddToCart.setVisibility(MaterialButton.GONE);
        } else {
            btnAddToCart.setVisibility(MaterialButton.VISIBLE);
            btnAddToCart.setOnClickListener(v -> onAddToCartClicked());
        }

        // Edit chỉ dành cho owner
        if (isOwner) {
            btnEdit.setVisibility(MaterialButton.VISIBLE);
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(PostDetailActivity.this, PostEditActivity.class);
                intent.putExtra(PostEditActivity.EXTRA_POST_ID, currentPost.getId());
                startActivity(intent);
            });
        } else {
            btnEdit.setVisibility(MaterialButton.GONE);
        }

        // Like / Unlike
        btnLike.setOnClickListener(v -> onLikeToggle());

        // Gửi comment
        btnSendComment.setOnClickListener(v -> onSendComment());
    }

    private void updateLikeButton() {
        btnLike.setText(isFavourited ? "Unlike" : "Like");
    }

    private void onLikeToggle() {
        int uid = sessionManager.getUserId();
        if (uid == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPost == null) return;

        if (isFavourited) {
            // Unlike
            favouriteRepository.removeFavouriteAsync(currentPost.getId(), uid, () -> {
                currentPost.setLikeCount(Math.max(0, currentPost.getLikeCount() - 1));
                new Thread(() -> {
                    postRepository.updatePost(currentPost);
                    runOnUiThread(() -> {
                        isFavourited = false;
                        tvLikes.setText(currentPost.getLikeCount() + " likes");
                        updateLikeButton();
                        Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            });
        } else {
            // Like
            favouriteRepository.addFavouriteAsync(currentPost.getId(), uid, () -> {
                currentPost.setLikeCount(currentPost.getLikeCount() + 1);
                new Thread(() -> {
                    postRepository.updatePost(currentPost);
                    runOnUiThread(() -> {
                        isFavourited = true;
                        tvLikes.setText(currentPost.getLikeCount() + " likes");
                        updateLikeButton();
                        Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            });
        }
    }

    private void onSendComment() {
        int uid = sessionManager.getUserId();
        if (uid == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = etComment.getText() != null ? etComment.getText().toString().trim() : "";
        if (content.isEmpty()) return;

        Comment c = new Comment();
        c.setContent(content);
        c.setUserId(uid);
        c.setPostId(currentPost.getId());

        commentRepository.addCommentAsync(c, id -> runOnUiThread(() -> {
            // Không truy vấn username đồng bộ trên UI thread nữa
            String header = "User#" + uid + " · " + (c.getCommentDate() != null ? sdf.format(c.getCommentDate()) : "");
            commentAdapter.addItem(new CommentAdapter.CommentDisplay(header, content));
            etComment.setText("");
        }));
    }

    private void onAddToCartClicked() {
        if (currentPost == null) return;
        
        String url = currentPost.getImagePath();
        if (currentPost.getPrice() <= 0) {
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(this, "No image URL", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            int userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }
            cartViewModel.addToCart(userId, currentPost.getId());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
