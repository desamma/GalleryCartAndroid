package com.example.gallerycart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.ArtistAdapter;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.viewmodel.ArtistViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllArtistsActivity extends AppCompatActivity {

    private ArtistViewModel artistViewModel;
    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private SearchView searchView;
    private List<User> allArtists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_artists);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artistAdapter = new ArtistAdapter(new ArrayList<>());
        recyclerView.setAdapter(artistAdapter);

        // Set up search view
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterArtists(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterArtists(newText);
                return true;
            }
        });

        // Set up click listeners for adapter
        artistAdapter.setOnArtistClickListener(new ArtistAdapter.OnArtistClickListener() {
            @Override
            public void onViewProfile(User artist) {
                // TODO: Artist Profile
//                Intent intent = new Intent(AllArtistsActivity.this, ArtistProfileActivity.class);
//                intent.putExtra("artist_id", artist.getId());
//                startActivity(intent);
            }

            @Override
            public void onRequestCommission(User artist) {
                if (artist.getCommissionStatus() == 1) {
                    Intent intent = new Intent(AllArtistsActivity.this, CommissionRequestActivity.class);
                    intent.putExtra("ARTIST_ID", artist.getId());
                    intent.putExtra("ARTIST_NAME", artist.getUsername());
                    intent.putExtra("ARTIST_DESCRIPTION", artist.getProfessionSummary());
                    startActivity(intent);
                } else {
                    String status = artist.getCommissionStatus() == 0 ? "closed" : "full";
                    Toast.makeText(AllArtistsActivity.this,
                            "This artist's commissions are " + status,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMessage(User artist) {
                //TODO: real time message
//                Intent intent = new Intent(AllArtistsActivity.this, MessageActivity.class);
//                intent.putExtra("recipient_id", artist.getId());
//                startActivity(intent);
            }
        });

        // Observe artists from ViewModel
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistViewModel.getAllArtists().observe(this, artists -> {
            if (artists != null) {
                allArtists = new ArrayList<>(artists);
                artistAdapter.setArtists(artists);

                // Show message if no artists found
                if (artists.isEmpty()) {
                    Toast.makeText(this, "No artists found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Filter button (optional - implement filter dialog)
        findViewById(R.id.filterButton).setOnClickListener(v -> {
            // Show filter dialog
            showFilterDialog();
        });
    }

    private void filterArtists(String query) {
        if (query == null || query.trim().isEmpty()) {
            artistAdapter.setArtists(allArtists);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<User> filtered = allArtists.stream()
                .filter(artist -> {
                    String name = artist.getUsername() != null ? artist.getUsername().toLowerCase() : "";
                    String email = artist.getEmail() != null ? artist.getEmail().toLowerCase() : "";
                    String summary = artist.getProfessionSummary() != null ?
                            artist.getProfessionSummary().toLowerCase() : "";

                    return name.contains(lowerQuery) ||
                            email.contains(lowerQuery) ||
                            summary.contains(lowerQuery);
                })
                .collect(Collectors.toList());

        artistAdapter.setArtists(filtered);
    }

    private void showFilterDialog() {
        // TODO: Implement filter dialog
        // Options: Commission status, Skills, Software, etc.
        Toast.makeText(this, "Filter feature coming soon", Toast.LENGTH_SHORT).show();
    }
}
