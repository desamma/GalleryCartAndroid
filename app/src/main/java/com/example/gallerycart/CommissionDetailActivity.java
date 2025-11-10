package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gallerycart.data.entity.Commission;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.viewmodel.CommissionViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommissionDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvDescription, tvPrice, tvDeadline, tvCreatedAt;
    private TextView tvAcceptedAt, tvCompletedAt, tvWorkLink;
    private Button btnAccept, btnReject, btnStart, btnComplete, btnCancel, btnOpenWorkLink;

    private CommissionViewModel commissionViewModel;
    private SessionManager sessionManager;
    private int commissionId;
    private Commission currentCommission;
    private boolean isArtistView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_detail);

        sessionManager = new SessionManager(this);
        commissionViewModel = new ViewModelProvider(this).get(CommissionViewModel.class);

        initViews();
        loadCommissionData();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvAcceptedAt = findViewById(R.id.tvAcceptedAt);
        tvCompletedAt = findViewById(R.id.tvCompletedAt);
        tvWorkLink = findViewById(R.id.tvWorkLink);

        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnStart = findViewById(R.id.btnStart);
        btnComplete = findViewById(R.id.btnComplete);
        btnCancel = findViewById(R.id.btnCancel);
        btnOpenWorkLink = findViewById(R.id.btnOpenWorkLink);
    }

    private void loadCommissionData() {
        commissionId = getIntent().getIntExtra("COMMISSION_ID", -1);
        if (commissionId == -1) {
            Toast.makeText(this, "Invalid commission", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        commissionViewModel.getCommissionById(commissionId).observe(this, commission -> {
            if (commission != null) {
                currentCommission = commission;
                int currentUserId = sessionManager.getUserId();
                isArtistView = currentCommission.getArtistId() == currentUserId;
                displayCommissionData();
            } else {
                Toast.makeText(this, "Commission not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayCommissionData() {
        tvStatus.setText(currentCommission.getStatus());
        tvStatus.setBackgroundColor(getStatusColor(currentCommission.getStatus()));

        tvDescription.setText(currentCommission.getDescription());
        tvPrice.setText(String.format(Locale.getDefault(), "%.2f VND", currentCommission.getPrice()));

        if (currentCommission.getDeadline() != null) {
            tvDeadline.setText("Deadline: " + dateFormat.format(currentCommission.getDeadline()));
        }

        if (currentCommission.getCreatedAt() != null) {
            tvCreatedAt.setText("Created: " + dateFormat.format(currentCommission.getCreatedAt()));
        }

        if (currentCommission.getAcceptedAt() != null) {
            tvAcceptedAt.setText("Accepted: " + dateFormat.format(currentCommission.getAcceptedAt()));
            tvAcceptedAt.setVisibility(View.VISIBLE);
        } else {
            tvAcceptedAt.setVisibility(View.GONE);
        }

        if (currentCommission.getCompletedAt() != null) {
            tvCompletedAt.setText("Completed: " + dateFormat.format(currentCommission.getCompletedAt()));
            tvCompletedAt.setVisibility(View.VISIBLE);
        } else {
            tvCompletedAt.setVisibility(View.GONE);
        }

        if (currentCommission.getWorkLink() != null && !currentCommission.getWorkLink().isEmpty()) {
            tvWorkLink.setText("Work Link: " + currentCommission.getWorkLink());
            tvWorkLink.setVisibility(View.VISIBLE);
            btnOpenWorkLink.setVisibility(View.VISIBLE);
        } else {
            tvWorkLink.setVisibility(View.GONE);
            btnOpenWorkLink.setVisibility(View.GONE);
        }

        configureButtons();
    }

    private void configureButtons() {
        String status = currentCommission.getStatus();

        if (isArtistView) {
            btnAccept.setVisibility(status.equals(Commission.STATUS_PENDING) ? View.VISIBLE : View.GONE);
            btnReject.setVisibility(status.equals(Commission.STATUS_PENDING) ? View.VISIBLE : View.GONE);
            btnStart.setVisibility(status.equals(Commission.STATUS_ACCEPTED) ? View.VISIBLE : View.GONE);
            btnComplete.setVisibility(status.equals(Commission.STATUS_IN_PROGRESS) ? View.VISIBLE : View.GONE);
            btnCancel.setVisibility(View.GONE);
        } else {
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            btnCancel.setVisibility(
                    status.equals(Commission.STATUS_ACCEPTED) || status.equals(Commission.STATUS_IN_PROGRESS)
                            ? View.VISIBLE : View.GONE
            );
        }
    }

    private void setupListeners() {
        btnAccept.setOnClickListener(v -> showConfirmDialog(
                "Accept Commission",
                "Are you sure you want to accept this commission?",
                () -> commissionViewModel.acceptCommission(commissionId)
        ));

        btnReject.setOnClickListener(v -> showConfirmDialog(
                "Reject Commission",
                "Are you sure you want to reject this commission?",
                () -> commissionViewModel.rejectCommission(commissionId)
        ));

        btnStart.setOnClickListener(v -> showConfirmDialog(
                "Start Commission",
                "Ready to start working on this commission?",
                () -> commissionViewModel.startCommission(commissionId)
        ));

        btnComplete.setOnClickListener(v -> showCompleteDialog());

        btnCancel.setOnClickListener(v -> showConfirmDialog(
                "Cancel Commission",
                "Are you sure you want to cancel this commission?",
                () -> commissionViewModel.cancelCommission(commissionId)
        ));

        btnOpenWorkLink.setOnClickListener(v -> {
            if (currentCommission.getWorkLink() != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentCommission.getWorkLink()));
                startActivity(browserIntent);
            }
        });
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
                .setNegativeButton("No", null)
                .show();
    }

    private void showCompleteDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_complete_commission, null);
        TextInputEditText etWorkLink = dialogView.findViewById(R.id.etWorkLink);

        new AlertDialog.Builder(this)
                .setTitle("Complete Commission")
                .setMessage("Please provide a link to your completed work")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String workLink = etWorkLink.getText().toString().trim();
                    if (workLink.isEmpty()) {
                        Toast.makeText(this, "Please enter a work link", Toast.LENGTH_SHORT).show();
                    } else {
                        commissionViewModel.completeCommission(commissionId, workLink);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        commissionViewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                if (result.success) {
                    // No need to call loadCommissionData() here, LiveData will update automatically
                }
            }
        });
    }

    private int getStatusColor(String status) {
        switch (status) {
            case Commission.STATUS_PENDING:
                return 0xFFFFA500;
            case Commission.STATUS_ACCEPTED:
                return 0xFF2196F3;
            case Commission.STATUS_REJECTED:
                return 0xFFF44336;
            case Commission.STATUS_IN_PROGRESS:
                return 0xFF9C27B0;
            case Commission.STATUS_COMPLETED:
                return 0xFF4CAF50;
            case Commission.STATUS_CANCELLED:
                return 0xFF757575;
            default:
                return 0xFF9E9E9E;
        }
    }
}
