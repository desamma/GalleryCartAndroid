package com.example.gallerycart;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.PurchasedItemAdapter;
import com.example.gallerycart.data.model.CartItemWithPost;
import com.example.gallerycart.repository.CartRepository;
import com.example.gallerycart.util.SessionManager;
import java.util.List;

public class MyPurchasesActivity extends AppCompatActivity {

    private RecyclerView rvPurchasedItems;
    private PurchasedItemAdapter adapter;
    private CartRepository cartRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        rvPurchasedItems = findViewById(R.id.rvPurchasedItems);
        rvPurchasedItems.setLayoutManager(new LinearLayoutManager(this));

        cartRepository = new CartRepository(this);
        sessionManager = new SessionManager(this);

        loadPurchasedItems();
    }

    private void loadPurchasedItems() {
        int userId = sessionManager.getUserId();
        new Thread(() -> {
            List<CartItemWithPost> purchasedItems = cartRepository.getPurchasedItems(userId);
            runOnUiThread(() -> {
                adapter = new PurchasedItemAdapter(this, purchasedItems);
                rvPurchasedItems.setAdapter(adapter);
            });
        }).start();
    }
}
