package com.example.gallerycart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.R;
import com.example.gallerycart.data.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<User> artists;
    private OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onViewProfile(User artist);
        void onRequestCommission(User artist);
        void onMessage(User artist);
    }

    public ArtistAdapter(List<User> artists) {
        this.artists = artists != null ? artists : new ArrayList<>();
    }

    public void setOnArtistClickListener(OnArtistClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        User artist = artists.get(position);

        // Set artist name
        holder.artistName.setText(artist.getUsername() != null ? artist.getUsername() : "Unknown Artist");

        // Set artist email
        holder.artistEmail.setText(artist.getEmail() != null ? artist.getEmail() : "");

        // Set profession summary
        if (artist.getProfessionSummary() != null && !artist.getProfessionSummary().isEmpty()) {
            holder.artistAbout.setText(artist.getProfessionSummary());
            holder.artistAbout.setVisibility(View.VISIBLE);
        } else {
            holder.artistAbout.setVisibility(View.GONE);
        }

        // Set skills
        if (artist.getSkills() != null && !artist.getSkills().isEmpty()) {
            String skillsText = "Skills: " + String.join(", ", artist.getSkills());
            holder.artistSkills.setText(skillsText);
            holder.artistSkills.setVisibility(View.VISIBLE);
        } else {
            holder.artistSkills.setVisibility(View.GONE);
        }

        // Set software
        if (artist.getSoftware() != null && !artist.getSoftware().isEmpty()) {
            String softwareText = "Software: " + String.join(", ", artist.getSoftware());
            holder.artistSoftware.setText(softwareText);
            holder.artistSoftware.setVisibility(View.VISIBLE);
        } else {
            holder.artistSoftware.setVisibility(View.GONE);
        }

        // Set commission status indicator (optional)
        String commissionText = getCommissionStatusText(artist.getCommissionStatus());

        // Set button click listeners
        holder.viewProfileButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewProfile(artist);
            }
        });

        holder.requestCommissionButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestCommission(artist);
            }
        });

        holder.messageButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMessage(artist);
            }
        });

        // Disable commission button if commissions are closed or full
        holder.requestCommissionButton.setEnabled(artist.getCommissionStatus() == 1);
        holder.requestCommissionButton.setAlpha(artist.getCommissionStatus() == 1 ? 1.0f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return artists != null ? artists.size() : 0;
    }

    public void setArtists(List<User> artists) {
        this.artists = artists != null ? artists : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String getCommissionStatusText(int status) {
        switch (status) {
            case 0: return "Closed";
            case 1: return "Open";
            case 2: return "Full";
            default: return "Unknown";
        }
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {

        ImageView artistAvatar;
        TextView artistName;
        TextView artistEmail;
        TextView artistAbout;
        TextView artistSkills;
        TextView artistSoftware;
        Button viewProfileButton;
        Button requestCommissionButton;
        Button messageButton;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistAvatar = itemView.findViewById(R.id.artistAvatar);
            artistName = itemView.findViewById(R.id.artistName);
            artistEmail = itemView.findViewById(R.id.artistEmail);
            artistAbout = itemView.findViewById(R.id.artistAbout);
            artistSkills = itemView.findViewById(R.id.artistSkills);
            artistSoftware = itemView.findViewById(R.id.artistSoftware);
            viewProfileButton = itemView.findViewById(R.id.viewProfileButton);
            requestCommissionButton = itemView.findViewById(R.id.requestCommissionButton);
            messageButton = itemView.findViewById(R.id.messageButton);
        }
    }
}