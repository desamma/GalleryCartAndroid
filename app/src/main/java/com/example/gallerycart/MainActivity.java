package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For testing - show welcome message
        Toast.makeText(this, "Welcome to Gallery Cart!", Toast.LENGTH_LONG).show();
    }
}