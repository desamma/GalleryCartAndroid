package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.repository.UserRepository;
import com.example.gallerycart.util.CloudinaryUploader;
import com.example.gallerycart.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PostEditActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";
    private static final int REQUEST_PICK_IMAGE = 1001;

    private EditText etTitle;
    private EditText etDescription;
    private EditText etImageUrl;
    private EditText etPrice;
    private SwitchMaterial switchMature;
    private SwitchMaterial switchPortfolio;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;
    private MaterialButton btnSelectImage;
    private ImageView ivPreview;

    private SessionManager sessionManager;
    private PostRepository postRepository;
    private CloudinaryUploader cloudinaryUploader;

    private boolean isEditMode = false;
    private Post currentPost;
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        Toolbar toolbar = findViewById(R.id.toolbarPostEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        String cloudName = getString(R.string.cloudinary_cloud_name);
        String preset = getString(R.string.cloudinary_unsigned_preset);
        cloudinaryUploader = new CloudinaryUploader(cloudName, preset);

        postRepository = new PostRepository(this);

        initViews();

        int userId = sessionManager.getUserId();
        new Thread(() -> {
            UserRepository userRepository = new UserRepository(getApplicationContext());
            User user = userRepository.getUserById(userId);

            boolean isArtist = user != null &&
                    ("artist".equalsIgnoreCase(user.getRole()) || user.isArtist());

            if (!isArtist) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Only artists can create posts", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();

        int postId = getIntent().getIntExtra(EXTRA_POST_ID, -1);
        isEditMode = postId != -1;

        if (isEditMode) {
            loadPost(postId);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Create post");
            }
            btnDelete.setEnabled(false);
            btnDelete.setAlpha(0.4f);
        }

        btnSave.setOnClickListener(v -> onSaveClicked());
        btnDelete.setOnClickListener(v -> onDeleteClicked());
        btnSelectImage.setOnClickListener(v -> pickImage());
    }

    private void initViews() {
        etTitle = findViewById(R.id.etPostTitle);
        etDescription = findViewById(R.id.etPostDescription);
        etImageUrl = findViewById(R.id.etPostImageUrl);
        etPrice = findViewById(R.id.etPostPrice);
        switchMature = findViewById(R.id.switchMature);
        switchPortfolio = findViewById(R.id.switchPortfolio);
        btnSave = findViewById(R.id.btnSavePost);
        btnDelete = findViewById(R.id.btnDeletePost);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivPreview = findViewById(R.id.ivPreview);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                uploadToCloudinary(uri);
            }
        }
    }

    private void uploadToCloudinary(Uri uri) {
        if (isUploading) return;
        isUploading = true;
        btnSave.setEnabled(false);
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        cloudinaryUploader.uploadUri(this, uri, new CloudinaryUploader.UploadCallback() {
            @Override
            public void onSuccess(String secureUrl) {
                runOnUiThread(() -> {
                    isUploading = false;
                    btnSave.setEnabled(true);
                    if (secureUrl != null) {
                        etImageUrl.setText(secureUrl);
                        Glide.with(PostEditActivity.this)
                                .load(secureUrl)
                                .placeholder(R.drawable.defaultavatar)
                                .into(ivPreview);
                        Toast.makeText(PostEditActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostEditActivity.this, "Upload success but URL empty", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    isUploading = false;
                    btnSave.setEnabled(true);
                    Toast.makeText(PostEditActivity.this, "Upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadPost(int postId) {
        postRepository.getPostByIdAsync(postId, post -> {
            if (post == null) {
                Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            int currentUserId = sessionManager.getUserId();
            if (currentUserId != post.getUserId()) {
                Toast.makeText(this, "You are not allowed to edit this post", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            currentPost = post;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit post");
            }

            etTitle.setText(post.getTitle());
            etDescription.setText(post.getDescription());
            etImageUrl.setText(post.getImagePath());
            etPrice.setText(String.valueOf(post.getPrice()));
            switchMature.setChecked(post.isMature());
            switchPortfolio.setChecked(post.isPortfolio());

            String url = post.getImagePath();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.defaultavatar)
                        .into(ivPreview);
            }
        });
    }

    private void onSaveClicked() {
        if (isUploading) {
            Toast.makeText(this, "Please wait, image is uploading...", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        boolean isMature = switchMature.isChecked();
        boolean isPortfolio = switchPortfolio.isChecked();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            etImageUrl.setError("Image URL is required");
            return;
        }

        double price = 0.0;
        if (!TextUtils.isEmpty(priceStr)) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                etPrice.setError("Invalid price");
                return;
            }
        }
        if (price < 0) {
            etPrice.setError("Price cannot be negative");
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        final String finalTitle = title;
        final String finalDesc = desc;
        final String finalImageUrl = imageUrl;
        final boolean finalIsMature = isMature;
        final boolean finalIsPortfolio = isPortfolio;
        final double finalPrice = price;
        final int finalUserId = userId;

        if (isEditMode && currentPost != null) {
            new Thread(() -> {
                currentPost.setTitle(finalTitle);
                currentPost.setDescription(finalDesc);
                currentPost.setImagePath(finalImageUrl); // 1 URL duy nhất
                currentPost.setPrice(finalPrice);
                currentPost.setMature(finalIsMature);
                currentPost.setPortfolio(finalIsPortfolio);

                postRepository.updatePost(currentPost);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Post updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        } else {
            new Thread(() -> {
                try {
                    postRepository.createPost(
                            finalTitle,
                            finalDesc,
                            finalImageUrl,
                            finalPrice,
                            finalIsMature,
                            finalIsPortfolio,
                            finalUserId
                    );
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        }
    }

    private void onDeleteClicked() {
        if (!isEditMode || currentPost == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        postRepository.deletePost(currentPost.getId());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Post deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(PostEditActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
