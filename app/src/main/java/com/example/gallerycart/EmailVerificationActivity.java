package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.UserDao;
import com.example.gallerycart.service.EmailService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailVerificationActivity extends AppCompatActivity {

    private TextView tvEmailAddress, tvBackToLogin;
    private TextInputLayout tilVerificationCode;
    private TextInputEditText etVerificationCode;
    private MaterialButton btnVerify, btnResendEmail;
    private ProgressBar progressBar;

    private String userEmail;
    private int userId;
    private String username;
    private String verificationToken;

    private UserDao userDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        userEmail = getIntent().getStringExtra("email");
        userId = getIntent().getIntExtra("userId", -1);
        username = getIntent().getStringExtra("username");
        verificationToken = getIntent().getStringExtra("token");

        initViews();
        setupDatabase();
        setupListeners();

        tvEmailAddress.setText(userEmail);
    }

    private void initViews() {
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        tilVerificationCode = findViewById(R.id.tilVerificationCode);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);
        btnResendEmail = findViewById(R.id.btnResendEmail);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupDatabase() {
        userDao = AppDatabase.getInstance(this).userDao();
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> verifyEmail());

        btnResendEmail.setOnClickListener(v -> resendEmail());

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(EmailVerificationActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void verifyEmail() {
        String code = etVerificationCode.getText().toString().trim();

        if (code.isEmpty()) {
            tilVerificationCode.setError("Please enter verification code");
            return;
        }

        tilVerificationCode.setError(null);
        showLoading();

        executorService.execute(() -> {
            try {
                // Verify the token
                Integer tokenUserId = EmailService.verifyToken(code);

                if (tokenUserId != null && tokenUserId == userId) {
                    // Update email confirmation status
                    userDao.updateEmailConfirmation(userId, true);
                    EmailService.removeToken(code);

                    // Send welcome email
                    EmailService.sendWelcomeEmail(userEmail, username);

                    runOnUiThread(() -> {
                        hideLoading();
                        Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to main activity
                        Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        hideLoading();
                        tilVerificationCode.setError("Invalid verification code");
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void resendEmail() {
        btnResendEmail.setEnabled(false);
        Toast.makeText(this, "Resending verification email...", Toast.LENGTH_SHORT).show();

        // Generate new token
        String newToken = EmailService.generateVerificationToken(userId);
        verificationToken = newToken;

        EmailService.sendVerificationEmail(userEmail, username, newToken);

        btnResendEmail.postDelayed(() -> btnResendEmail.setEnabled(true), 30000);

        Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnVerify.setEnabled(false);
        btnVerify.setText("");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnVerify.setEnabled(true);
        btnVerify.setText("Verify Email");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}