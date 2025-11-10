package com.example.gallerycart;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewWorkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work);

        TextView tvWorkLink = findViewById(R.id.tvWorkLink);
        String workLink = getIntent().getStringExtra("WORK_LINK");

        if (workLink != null && !workLink.isEmpty()) {
            tvWorkLink.setText(workLink);
        } else {
            tvWorkLink.setText("No work link provided.");
        }
    }
}