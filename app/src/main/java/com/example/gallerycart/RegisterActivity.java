package com.example.gallerycart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gallerycart.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputLayout tilDateOfBirth, tilProfessionSummary, tilSkills, tilSoftware, tilContactInfo;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private TextInputEditText etDateOfBirth, etProfessionSummary, etSkills, etSoftware, etContactInfo;
    private SwitchMaterial switchIsArtist;
    private LinearLayout layoutArtistFields;
    private RadioGroup rgCommissionStatus;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView tvLoginLink;
    private AuthViewModel authViewModel;

    private Calendar selectedDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);
        tilProfessionSummary = findViewById(R.id.tilProfessionSummary);
        tilSkills = findViewById(R.id.tilSkills);
        tilSoftware = findViewById(R.id.tilSoftware);
        tilContactInfo = findViewById(R.id.tilContactInfo);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etProfessionSummary = findViewById(R.id.etProfessionSummary);
        etSkills = findViewById(R.id.etSkills);
        etSoftware = findViewById(R.id.etSoftware);
        etContactInfo = findViewById(R.id.etContactInfo);

        switchIsArtist = findViewById(R.id.switchIsArtist);
        layoutArtistFields = findViewById(R.id.layoutArtistFields);
        rgCommissionStatus = findViewById(R.id.rgCommissionStatus);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        selectedDate = Calendar.getInstance();
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthResult().observe(this, result -> {
            hideLoading();

            if (result.success) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                // Navigate to main activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        switchIsArtist.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutArtistFields.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnRegister.setOnClickListener(v -> performRegister());

        tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    etDateOfBirth.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -0);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void performRegister() {
        // Clear all errors
        clearErrors();

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String dateOfBirth = etDateOfBirth.getText().toString();
        boolean isArtist = switchIsArtist.isChecked();

        if (username.isEmpty()) {
            tilUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (dateOfBirth.isEmpty()) {
            tilDateOfBirth.setError("Date of birth is required");
            etDateOfBirth.requestFocus();
            return;
        }

        AuthViewModel.RegisterData registerData = new AuthViewModel.RegisterData();
        registerData.username = username;
        registerData.email = email;
        registerData.password = password;
        registerData.confirmPassword = confirmPassword;
        registerData.dateOfBirth = selectedDate.getTime();
        registerData.isArtist = isArtist;

        // If artist, get additional fields
        if (isArtist) {
            String professionSummary = etProfessionSummary.getText().toString().trim();
            String skillsText = etSkills.getText().toString().trim();
            String softwareText = etSoftware.getText().toString().trim();
            String contactInfo = etContactInfo.getText().toString().trim();

            if (professionSummary.isEmpty()) {
                tilProfessionSummary.setError("Profession summary is required");
                etProfessionSummary.requestFocus();
                return;
            }

            registerData.professionSummary = professionSummary;
            registerData.skills = parseCommaSeparated(skillsText);
            registerData.software = parseCommaSeparated(softwareText);
            registerData.contactInfo = contactInfo.isEmpty() ? email : contactInfo;
            registerData.commissionStatus = getCommissionStatus();
        }

        showLoading();
        authViewModel.register(registerData);
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

    private int getCommissionStatus() {
        int selectedId = rgCommissionStatus.getCheckedRadioButtonId();
        if (selectedId == R.id.rbOpen) {
            return 1;
        } else if (selectedId == R.id.rbFull) {
            return 2;
        }
        return 0; // Close
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilDateOfBirth.setError(null);
        tilProfessionSummary.setError(null);
        tilSkills.setError(null);
        tilSoftware.setError(null);
        tilContactInfo.setError(null);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        btnRegister.setText("");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        btnRegister.setText("Register");
    }
}