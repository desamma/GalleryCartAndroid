package com.example.gallerycart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.CartAdapter;
import com.example.gallerycart.data.model.CartItemWithPost;
import com.example.gallerycart.repository.CartRepository;
import com.example.gallerycart.util.SessionManager;
import com.example.gallerycart.util.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private CartRepository cartRepo;
    private SessionManager sessionManager;
    private RecyclerView rv;
    private CartAdapter adapter;
    private TextView tvTotal;
    private Button btnCheckout;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_cart);
        cartRepo = new CartRepository(this);
        sessionManager = new SessionManager(this);

        rv = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new CartAdapter(this, new CartAdapter.Listener() {
            @Override
            public void onRemove(CartItemWithPost item) {
                executor.execute(() -> {
                    if (item.getCartItem() != null) {
                        cartRepo.removeCartItem(item.getCartItem().getId(), item.getCartItem().getCartId());
                    }
                    runOnUiThread(() -> loadCart());
                });
            }
        });


        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> executor.execute(() -> {
            try {
                cartRepo.checkoutCart(sessionManager.getUserId());
                runOnUiThread(() -> {
                    Utils.showToast(this, "Checkout success");
                    loadCart();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Utils.showToast(this, "Checkout failed: " + e.getMessage()));
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        executor.execute(() -> {
            List<CartItemWithPost> items = cartRepo.getCartItemsWithPosts(sessionManager.getUserId());
            double total = 0;
            if (items != null) {
                for (CartItemWithPost it : items) {
                    total += it.getPrice() * it.getCartItem().getQuantity();
                }
            }

            final double finalTotal = total;
            runOnUiThread(() -> {
                adapter.setData(items);
                tvTotal.setText(String.format("Total: %.0f", finalTotal) + "VND");
            });
        });
    }

}

