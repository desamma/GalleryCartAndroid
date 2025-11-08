package com.example.gallerycart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.adapter.ArtistAdapter;
import com.example.gallerycart.viewmodel.ArtistViewModel;
import java.util.ArrayList;

public class AllArtistsActivity extends AppCompatActivity {

    private ArtistViewModel artistViewModel;
    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_artists);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artistAdapter = new ArtistAdapter(new ArrayList<>());
        recyclerView.setAdapter(artistAdapter);

        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistViewModel.getAllArtists().observe(this, artists -> {
            artistAdapter.setArtists(artists);
        });
    }
}
