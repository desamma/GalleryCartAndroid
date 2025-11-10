package com.example.gallerycart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.CommissionAdapter;
import com.example.gallerycart.data.entity.Commission;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.viewmodel.CommissionViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AllCommissionsActivity extends AppCompatActivity {

    private CommissionViewModel commissionViewModel;
    private RecyclerView recyclerView;
    private CommissionAdapter commissionAdapter;
    private SessionManager sessionManager;
    private boolean isArtistView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_commissions);

        sessionManager = new SessionManager(this);

        // Check if viewing as artist or client
        isArtistView = getIntent().getBooleanExtra("IS_ARTIST_VIEW", false);

        setupRecyclerView();
        setupViewModel();
        observeViewModel();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commissionAdapter = new CommissionAdapter(this, isArtistView);
        recyclerView.setAdapter(commissionAdapter);

        commissionAdapter.setOnCommissionActionListener(new CommissionAdapter.OnCommissionActionListener() {
            @Override
            public void onViewDetails(Commission commission) {
                Intent intent = new Intent(AllCommissionsActivity.this, CommissionDetailActivity.class);
                intent.putExtra("COMMISSION_ID", commission.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(Commission commission) {
                Intent intent = new Intent(AllCommissionsActivity.this, CommissionFormActivity.class);
                intent.putExtra("COMMISSION_ID", commission.getId());
                intent.putExtra("ARTIST_ID", commission.getArtistId());
                startActivityForResult(intent, 100);
            }

            @Override
            public void onDelete(Commission commission) {
                new AlertDialog.Builder(AllCommissionsActivity.this)
                        .setTitle("Delete Commission")
                        .setMessage("Are you sure you want to delete this commission?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            commissionViewModel.delete(commission);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onAccept(Commission commission) {
                new AlertDialog.Builder(AllCommissionsActivity.this)
                        .setTitle("Accept Commission")
                        .setMessage("Are you sure you want to accept this commission?")
                        .setPositiveButton("Accept", (dialog, which) -> {
                            commissionViewModel.acceptCommission(commission.getId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onReject(Commission commission) {
                new AlertDialog.Builder(AllCommissionsActivity.this)
                        .setTitle("Reject Commission")
                        .setMessage("Are you sure you want to reject this commission?")
                        .setPositiveButton("Reject", (dialog, which) -> {
                            commissionViewModel.rejectCommission(commission.getId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onStart(Commission commission) {
                new AlertDialog.Builder(AllCommissionsActivity.this)
                        .setTitle("Start Commission")
                        .setMessage("Ready to start working on this commission?")
                        .setPositiveButton("Start", (dialog, which) -> {
                            commissionViewModel.startCommission(commission.getId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onComplete(Commission commission) {
                showCompleteDialog(commission);
            }

            @Override
            public void onCancel(Commission commission) {
                new AlertDialog.Builder(AllCommissionsActivity.this)
                        .setTitle("Cancel Commission")
                        .setMessage("Are you sure you want to cancel this commission?")
                        .setPositiveButton("Cancel Commission", (dialog, which) -> {
                            commissionViewModel.cancelCommission(commission.getId());
                        })
                        .setNegativeButton("Back", null)
                        .show();
            }

            @Override
            public void onOpenWorkLink(Commission commission) {
                if (commission.getWorkLink() != null && !commission.getWorkLink().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commission.getWorkLink()));
                    startActivity(browserIntent);
                }
            }
        });
    }

    private void setupViewModel() {
        commissionViewModel = new ViewModelProvider(this).get(CommissionViewModel.class);

        int userId = sessionManager.getUserId();
        if (isArtistView) {
            commissionViewModel.initForArtist(userId);
        } else {
            commissionViewModel.initForClient(userId);
        }
    }

    private void observeViewModel() {
        commissionViewModel.getAllCommissions().observe(this, commissions -> {
            commissionAdapter.setCommissions(commissions);

            if (commissions == null || commissions.isEmpty()) {
                Toast.makeText(this, "No commissions found", Toast.LENGTH_SHORT).show();
            }
        });

        commissionViewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompleteDialog(Commission commission) {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_complete_commission, null);
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
                        commissionViewModel.completeCommission(commission.getId(), workLink);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh list after edit
            Toast.makeText(this, "Commission updated", Toast.LENGTH_SHORT).show();
        }
    }
}