package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                // Navigate to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
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
        // Clear errors
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