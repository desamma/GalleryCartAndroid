package com.example.gallerycart;

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
import com.example.gallerycart.viewmodel.CommissionViewModel;

public class CommissionRequestActivity extends AppCompatActivity {

    private ImageView ivArtistAvatar;
    private TextView tvArtistName, tvArtistDescription;
    private EditText etDescription, etPrice, etDeadline;
    private Button btnSendRequest;
    private String artistId;
    private CommissionViewModel commissionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_request);

        ivArtistAvatar = findViewById(R.id.ivArtistAvatar);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvArtistDescription = findViewById(R.id.tvArtistDescription);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etDeadline = findViewById(R.id.etDeadline);
        btnSendRequest = findViewById(R.id.btnSendRequest);

        Intent intent = getIntent();
        artistId = intent.getStringExtra("ARTIST_ID");
        String artistName = intent.getStringExtra("ARTIST_NAME");
        String artistDescription = intent.getStringExtra("ARTIST_DESCRIPTION");

        // TODO: Get the client ID from shared preferences or intent
        String clientId = "1";
        commissionViewModel = new ViewModelProvider(this).get(CommissionViewModel.class);
        commissionViewModel.init(clientId);

        tvArtistName.setText(artistName != null ? artistName : "Artist Name Not Found");
        tvArtistDescription.setText(artistDescription != null ? artistDescription : "No description available.");
        // You can also load the artist's avatar here if you have the URL

        btnSendRequest.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String deadline = etDeadline.getText().toString().trim();

            if (description.isEmpty() || priceStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            Commission commission = new Commission();
            commission.setArtistId(artistId);
            commission.setClientId(clientId); // Replace with actual client ID
            commission.setDescription(description);
            commission.setPrice(price);
            commission.setDeadline(deadline);
            commission.setStatus("Pending");
            commission.setCreatedAt(System.currentTimeMillis());

            commissionViewModel.insert(commission);

            Toast.makeText(this, "Commission request sent!", Toast.LENGTH_SHORT).show();

            Intent allCommissionsIntent = new Intent(CommissionRequestActivity.this, AllCommissionsActivity.class);
            startActivity(allCommissionsIntent);
            finish();
        });
    }
}
