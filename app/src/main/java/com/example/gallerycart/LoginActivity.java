package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gallerycart.service.EmailService;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmailUsername, tilPassword;
    private TextInputEditText etEmailUsername, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegisterLink;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn() && sessionManager.getUserRole() != null) {
            if (sessionManager.getUserRole().equals("customer") || sessionManager.getUserRole().equals("artist")){
                navigateToMain();
            } else if (sessionManager.getUserRole().equals("admin")){
                navigateToAdmin();
            }
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        tilEmailUsername = findViewById(R.id.tilEmailUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etEmailUsername = findViewById(R.id.etEmailUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthResult().observe(this, result -> {
            hideLoading();

            if (result.success) {
                if (result.user != null) {
                    sessionManager.createLoginSession(result.user.getId(), result.user.getUsername(), result.user.getRole());
                }
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                if (sessionManager.getUserRole().equals("admin")){
                    navigateToAdmin();
                } else {
                    navigateToMain();
                }
            } else {
                if ("EMAIL_NOT_CONFIRMED".equals(result.message)) {
                    if (result.user != null) {
                        String token = com.example.gallerycart.service.EmailService
                                .generateVerificationToken(result.user.getId());

                        EmailService.sendVerificationEmail(
                                result.user.getEmail(),
                                result.user.getUsername(),
                                token
                        );

                        Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                        intent.putExtra("email", result.user.getEmail());
                        intent.putExtra("userId", result.user.getId());
                        intent.putExtra("username", result.user.getUsername());
                        intent.putExtra("token", token);
                        startActivity(intent);

                        Toast.makeText(this,
                                "Please verify your email before proceeding",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        tilEmailUsername.setError(null);
        tilPassword.setError(null);

        String emailOrUsername = etEmailUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (emailOrUsername.isEmpty()) {
            tilEmailUsername.setError("Email or username is required");
            etEmailUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        showLoading();
        authViewModel.login(emailOrUsername, password);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnLogin.setText("");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
    }
}