package com.example.gallerycart;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gallerycart.data.entity.Commission;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.viewmodel.CommissionViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommissionFormActivity extends AppCompatActivity {

    private ImageView ivArtistAvatar;
    private TextView tvArtistName, tvArtistDescription, tvFormTitle;
    private EditText etDescription, etPrice, etDeadline;
    private Button btnSubmit, btnCancel;

    private int artistId;
    private int commissionId = -1;
    private boolean isEditMode = false;
    private CommissionViewModel commissionViewModel;
    private SessionManager sessionManager;
    private Calendar selectedDeadline;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private Commission currentCommission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_form);

        sessionManager = new SessionManager(this);
        commissionViewModel = new ViewModelProvider(this).get(CommissionViewModel.class);
        selectedDeadline = Calendar.getInstance();
        selectedDeadline.add(Calendar.DAY_OF_MONTH, 7); // Default 7 days from now

        initViews();
        loadIntentData();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        ivArtistAvatar = findViewById(R.id.ivArtistAvatar);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvArtistDescription = findViewById(R.id.tvArtistDescription);
        tvFormTitle = findViewById(R.id.tvFormTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etDeadline = findViewById(R.id.etDeadline);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        artistId = intent.getIntExtra("ARTIST_ID", -1);
        String artistName = intent.getStringExtra("ARTIST_NAME");
        String artistDescription = intent.getStringExtra("ARTIST_DESCRIPTION");
        commissionId = intent.getIntExtra("COMMISSION_ID", -1);
        isEditMode = commissionId != -1;

        if (isEditMode) {
            tvFormTitle.setText("Edit Commission");
            btnSubmit.setText("Update Commission");
            loadCommissionData();
        } else {
            tvFormTitle.setText("Request Commission");
            btnSubmit.setText("Send Request");
        }

        tvArtistName.setText(artistName != null ? artistName : "Artist Name Not Found");
        tvArtistDescription.setText(artistDescription != null ? artistDescription : "No description available.");
        etDeadline.setText(dateFormat.format(selectedDeadline.getTime()));
    }

    private void loadCommissionData() {
        commissionViewModel.getCommissionById(commissionId).observe(this, commission -> {
            if (commission != null) {
                currentCommission = commission;
                etDescription.setText(commission.getDescription());
                etPrice.setText(String.valueOf(commission.getPrice()));
                if (commission.getDeadline() != null) {
                    selectedDeadline.setTime(commission.getDeadline());
                    etDeadline.setText(dateFormat.format(commission.getDeadline()));
                }
            }
        });
    }

    private void setupListeners() {
        etDeadline.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                submitCommission();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDeadline.set(Calendar.YEAR, year);
                    selectedDeadline.set(Calendar.MONTH, month);
                    selectedDeadline.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Show time picker after date is selected
                    showTimePicker();
                },
                selectedDeadline.get(Calendar.YEAR),
                selectedDeadline.get(Calendar.MONTH),
                selectedDeadline.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to tomorrow
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDeadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDeadline.set(Calendar.MINUTE, minute);
                    etDeadline.setText(dateFormat.format(selectedDeadline.getTime()));
                },
                selectedDeadline.get(Calendar.HOUR_OF_DAY),
                selectedDeadline.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private boolean validateInput() {
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (description.isEmpty()) {
            etDescription.setError("Please describe your commission");
            etDescription.requestFocus();
            return false;
        }

        if (priceStr.isEmpty()) {
            etPrice.setError("Please enter a price");
            etPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                etPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            etPrice.requestFocus();
            return false;
        }

        return true;
    }

    private void submitCommission() {
        String description = etDescription.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        int clientId = sessionManager.getUserId();

        if (isEditMode) {
            if (currentCommission != null) {
                currentCommission.setDescription(description);
                currentCommission.setPrice(price);
                currentCommission.setDeadline(selectedDeadline.getTime());
                commissionViewModel.update(currentCommission);
            }
        } else {
            // Create new commission
            Commission commission = new Commission();
            commission.setArtistId(artistId);
            commission.setClientId(clientId);
            commission.setDescription(description);
            commission.setPrice(price);
            commission.setDeadline(selectedDeadline.getTime());
            commission.setStatus(Commission.STATUS_PENDING);

            commissionViewModel.insert(commission);
        }
    }

    private void observeViewModel() {
        commissionViewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                if (result.success) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
}