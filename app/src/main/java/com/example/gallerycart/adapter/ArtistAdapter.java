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
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<User> artists;

    public ArtistAdapter(List<User> artists) {
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        User artist = artists.get(position);
        holder.artistName.setText(artist.getDisplayName());
        holder.artistEmail.setText(artist.getEmail());
        // Set other artist details as needed
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public void setArtists(List<User> artists) {
        this.artists = artists;
        notifyDataSetChanged();
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
