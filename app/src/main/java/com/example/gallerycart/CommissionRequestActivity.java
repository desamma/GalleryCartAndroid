package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CommissionRequestActivity extends AppCompatActivity {

    private ImageView ivArtistAvatar;
    private TextView tvArtistName, tvArtistDescription;
    private EditText etDescription, etPrice, etDeadline;
    private Button btnSendRequest;
    private String artistId;

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

        tvArtistName.setText(artistName);
        tvArtistDescription.setText(artistDescription);
        // You can also load the artist's avatar here if you have the URL

        btnSendRequest.setOnClickListener(v -> {
            // TODO: Implement commission request logic
            Toast.makeText(this, "Commission request sent!", Toast.LENGTH_SHORT).show();
        });
    }
}
