package com.example.gallerycart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.gallerycart.adapter.AdminCommissionAdapter;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.entity.Commission;

import java.util.List;

public class AdminCommissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminCommissionAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_commission);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "gallery-cart-db").build();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Commission> commissions = db.commissionDao().getAllCommissions();
            runOnUiThread(() -> {
                adapter = new AdminCommissionAdapter(commissions);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
