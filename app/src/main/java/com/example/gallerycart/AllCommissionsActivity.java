package com.example.gallerycart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.CommissionAdapter;
import com.example.gallerycart.viewmodel.CommissionViewModel;

public class AllCommissionsActivity extends AppCompatActivity {

    private CommissionViewModel commissionViewModel;
    private RecyclerView recyclerView;
    private CommissionAdapter commissionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_commissions);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commissionAdapter = new CommissionAdapter(this);
        recyclerView.setAdapter(commissionAdapter);

        // TODO: Get the client ID from shared preferences or intent
        String clientId = "1";

        commissionViewModel = new ViewModelProvider(this).get(CommissionViewModel.class);
        commissionViewModel.init(clientId);
        commissionViewModel.getAllCommissions().observe(this, commissions -> {
            commissionAdapter.setCommissions(commissions);
        });
    }
}
