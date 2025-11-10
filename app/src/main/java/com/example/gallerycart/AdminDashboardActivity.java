package com.example.gallerycart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.adapter.CartAdapter;
import com.example.gallerycart.adapter.SoldGroupedAdapter;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.model.CartItemWithPost;
import com.example.gallerycart.data.model.SectionItem;
import com.example.gallerycart.repository.CartRepository;
import com.example.gallerycart.repository.PostRepository;
import com.example.gallerycart.repository.UserRepository;
import com.example.gallerycart.util.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalPosts, tvRevenue;
    private EditText etFromDate, etToDate;
    private Button btnCheckRevenue, btnManageUsers, btnManagePosts, btnManageCommissions, btnLogout;

    private UserRepository userRepository;
    private PostRepository postRepository;
    private CartRepository cartRepository;

    private RecyclerView rvSoldCarts;
    private SoldGroupedAdapter soldAdapter;
    private final Calendar fromDate = Calendar.getInstance();
    private final Calendar toDate = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalPosts = findViewById(R.id.tvTotalPosts);
        tvRevenue = findViewById(R.id.tvRevenue);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnCheckRevenue = findViewById(R.id.btnCheckRevenue);
        rvSoldCarts = findViewById(R.id.rvSoldCarts);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManagePosts = findViewById(R.id.btnManagePosts);
        btnManageCommissions = findViewById(R.id.btnManageCommissions);
        btnLogout = findViewById(R.id.btnLogout);


        rvSoldCarts.setLayoutManager(new LinearLayoutManager(this));
        soldAdapter = new SoldGroupedAdapter(this);
        rvSoldCarts.setAdapter(soldAdapter);

        sessionManager = new SessionManager(this);

        userRepository = new UserRepository(this);
        postRepository = new PostRepository(this);
        cartRepository = new CartRepository(this);

        loadTotalUsers();
        loadTotalPosts();

        btnCheckRevenue.setOnClickListener(v -> checkRevenue());
        etFromDate.setOnClickListener(v -> showDateFromPicker());
        etToDate.setOnClickListener(v -> showDateToPicker());

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminUserActivity.class);
            startActivity(intent);
        });

        btnManageCommissions.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCommissionActivity.class);
            startActivity(intent);
        });

        /*btnManagePosts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminPostActivity.class);
            startActivity(intent);
        });*/

        btnLogout.setOnClickListener(v -> showLogoutDialog());

    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logout();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void loadTotalUsers() {
        userRepository.getAllUsers().observe(this, users -> {
            if (users != null) {
                tvTotalUsers.setText("Total Users: " + users.size());
            }
        });
    }

    private void showDateFromPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fromDate.set(year, month, dayOfMonth);
                    etFromDate.setText(dateFormat.format(fromDate.getTime()));
                },
                fromDate.get(Calendar.YEAR),
                fromDate.get(Calendar.MONTH),
                fromDate.get(Calendar.DAY_OF_MONTH)
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -0);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showDateToPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    toDate.set(year, month, dayOfMonth);
                    etToDate.setText(dateFormat.format(toDate.getTime()));
                },
                toDate.get(Calendar.YEAR),
                toDate.get(Calendar.MONTH),
                toDate.get(Calendar.DAY_OF_MONTH)
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -0);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void loadTotalPosts() {
        postRepository.getAllPostsAsync(posts -> runOnUiThread(() -> {
            if (posts != null) {
                tvTotalPosts.setText("Total Posts: " + posts.size());
            }
        }));
    }

    private void checkRevenue() {
        String fromStr = etFromDate.getText().toString();
        String toStr = etToDate.getText().toString();

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            Toast.makeText(this, "Please select both From and To dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date fromDateObj = dateFormat.parse(fromStr);
            long fromMillis = fromDateObj.getTime();

            Date toDateObj = dateFormat.parse(toStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDateObj);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long toMillis = calendar.getTimeInMillis();

            if (fromMillis > toMillis) {
                Toast.makeText(this, "\'From\' date cannot be after \'To\' date", Toast.LENGTH_SHORT).show();
                return;
            }

            loadRange(fromMillis, toMillis);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRange(long from, long to) {
        executor.execute(() -> {
            double rev = 0;
            List<CartItemWithPost> soldItems;

            try {
                rev = cartRepository.getRevenueBetween(from, to);
                soldItems = cartRepository.getSoldItemsBetween(from, to);
            } catch (Exception e) {
                e.printStackTrace();
                soldItems = java.util.Collections.emptyList();
            }

            if (soldItems == null) soldItems = java.util.Collections.emptyList();

            java.util.Map<Integer, List<CartItemWithPost>> map = new java.util.LinkedHashMap<>();
            for (CartItemWithPost it : soldItems) {
                int cartId = (it.getCartItem() != null) ? it.getCartItem().getCartId() : -1;
                map.computeIfAbsent(cartId, k -> new java.util.ArrayList<>()).add(it);
            }

            List<SectionItem> sections = new java.util.ArrayList<>();
            for (Integer cartId : map.keySet()) {
                List<CartItemWithPost> items = map.get(cartId);

                double cartTotal = 0d;
                for (CartItemWithPost ci : items) {
                    double price = ci.getPrice();
                    int q = (ci.getCartItem() != null) ? ci.getCartItem().getQuantity() : 0;
                    cartTotal += price * q;
                }

                long purchaseDateMillis = -1;
                try {
                    Cart cart = cartRepository.getCartById(cartId);
                    if (cart != null && cart.getPurchaseDate() != null) {
                        purchaseDateMillis = cart.getPurchaseDate().getTime();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                sections.add(new SectionItem(cartId, purchaseDateMillis, cartTotal));

                for (CartItemWithPost ci : items) {
                    sections.add(new SectionItem(ci));
                }
            }

            final double finalRev = rev;
            final List<SectionItem> finalSections = sections;

            runOnUiThread(() -> {
                tvRevenue.setText(String.format("Revenue: %.0f VND", finalRev));
                soldAdapter.setSections(finalSections);
            });
        });
    }
}
