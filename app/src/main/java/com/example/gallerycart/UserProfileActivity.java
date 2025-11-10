package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.UserRepository;
import com.example.gallerycart.util.CloudinaryUploader;
import com.example.gallerycart.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 2001;

    private ImageView ivUserAvatar;
    private TextView tvUsername, tvEmail, tvRole, tvCreatedDate;
    private EditText etPhoneNumber, etProfessionSummary, etSkills, etSoftware, etContactInfo;
    private SwitchMaterial switchCommissionStatus;
    private MaterialButton btnSelectAvatar, btnSaveProfile;
    private Button btnChangePassword, btnMyPurchases;

    private SessionManager sessionManager;
    private UserRepository userRepository;
    private CloudinaryUploader cloudinaryUploader;
    private ExecutorService executorService;

    private User currentUser;
    private boolean isUploading = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbarUserProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        sessionManager = new SessionManager(this);
        userRepository = new UserRepository(this);
        executorService = Executors.newSingleThreadExecutor();

        String cloudName = getString(R.string.cloudinary_cloud_name);
        String preset = getString(R.string.cloudinary_unsigned_preset);
        cloudinaryUploader = new CloudinaryUploader(cloudName, preset);

        initViews();
        loadUserProfile();
        setupListeners();
    }

    private void initViews() {
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etProfessionSummary = findViewById(R.id.etProfessionSummary);
        etSkills = findViewById(R.id.etSkills);
        etSoftware = findViewById(R.id.etSoftware);
        etContactInfo = findViewById(R.id.etContactInfo);
        switchCommissionStatus = findViewById(R.id.switchCommissionStatus);
        btnSelectAvatar = findViewById(R.id.btnSelectAvatar);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnMyPurchases = findViewById(R.id.btnMyPurchases);
    }

    private void loadUserProfile() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executorService.execute(() -> {
            currentUser = userRepository.getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    displayUserData();
                } else {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayUserData() {
        tvUsername.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "N/A");
        tvEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");

        String role = currentUser.getRole();
        if (currentUser.isArtist()) {
            role = "Artist";
        } else if ("admin".equalsIgnoreCase(role)) {
            role = "Admin";
        } else {
            role = "Customer";
        }
        tvRole.setText(role);

        if (currentUser.getCreatedDate() != null) {
            tvCreatedDate.setText("Member since " + dateFormat.format(currentUser.getCreatedDate()));
        }

        etPhoneNumber.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");

        // Load avatar
        String avatarUrl = currentUser.getUserAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.defaultavatar)
                    .into(ivUserAvatar);
        }

        // Artist-specific fields
        if (currentUser.isArtist()) {
            findViewById(R.id.layoutArtistProfile).setVisibility(android.view.View.VISIBLE);

            etProfessionSummary.setText(currentUser.getProfessionSummary() != null ?
                    currentUser.getProfessionSummary() : "");

            if (currentUser.getSkills() != null && !currentUser.getSkills().isEmpty()) {
                etSkills.setText(String.join(", ", currentUser.getSkills()));
            }

            if (currentUser.getSoftware() != null && !currentUser.getSoftware().isEmpty()) {
                etSoftware.setText(String.join(", ", currentUser.getSoftware()));
            }

            etContactInfo.setText(currentUser.getContactInfo() != null ?
                    currentUser.getContactInfo() : "");

            switchCommissionStatus.setChecked(currentUser.getCommissionStatus() == 1);
        } else {
            findViewById(R.id.layoutArtistProfile).setVisibility(android.view.View.GONE);
        }
    }

    private void setupListeners() {
        btnSelectAvatar.setOnClickListener(v -> pickImage());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password functionality
            Toast.makeText(this, "Change password feature coming soon", Toast.LENGTH_SHORT).show();
        });
        btnMyPurchases.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, MyPurchasesActivity.class);
            startActivity(intent);
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                uploadAvatar(uri);
            }
        }
    }

    private void uploadAvatar(Uri uri) {
        if (isUploading) return;
        isUploading = true;
        btnSaveProfile.setEnabled(false);
        Toast.makeText(this, "Uploading avatar...", Toast.LENGTH_SHORT).show();

        cloudinaryUploader.uploadUri(this, uri, new CloudinaryUploader.UploadCallback() {
            @Override
            public void onSuccess(String secureUrl) {
                runOnUiThread(() -> {
                    isUploading = false;
                    btnSaveProfile.setEnabled(true);
                    if (secureUrl != null) {
                        currentUser.setUserAvatar(secureUrl);
                        Glide.with(UserProfileActivity.this)
                                .load(secureUrl)
                                .placeholder(R.drawable.defaultavatar)
                                .into(ivUserAvatar);
                        Toast.makeText(UserProfileActivity.this, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    isUploading = false;
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(UserProfileActivity.this, "Upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveProfile() {
        if (isUploading) {
            Toast.makeText(this, "Please wait, avatar is uploading...", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = etPhoneNumber.getText().toString().trim();
        currentUser.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);

        if (currentUser.isArtist()) {
            String professionSummary = etProfessionSummary.getText().toString().trim();
            String skillsText = etSkills.getText().toString().trim();
            String softwareText = etSoftware.getText().toString().trim();
            String contactInfo = etContactInfo.getText().toString().trim();

            if (professionSummary.isEmpty()) {
                etProfessionSummary.setError("Profession summary is required for artists");
                etProfessionSummary.requestFocus();
                return;
            }

            currentUser.setProfessionSummary(professionSummary);
            currentUser.setSkills(parseCommaSeparated(skillsText));
            currentUser.setSoftware(parseCommaSeparated(softwareText));
            currentUser.setContactInfo(contactInfo.isEmpty() ? currentUser.getEmail() : contactInfo);
            currentUser.setCommissionStatus(switchCommissionStatus.isChecked() ? 1 : 0);
        }

        btnSaveProfile.setEnabled(false);
        executorService.execute(() -> {
            try {
                userRepository.updateUser(currentUser);
                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private List<String> parseCommaSeparated(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String[] items = text.split(",");
        List<String> result = new ArrayList<>();
        for (String item : items) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
