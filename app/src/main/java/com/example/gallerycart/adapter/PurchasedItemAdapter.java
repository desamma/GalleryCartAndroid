package com.example.gallerycart.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerycart.R;
import com.example.gallerycart.data.model.CartItemWithPost;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PurchasedItemAdapter extends RecyclerView.Adapter<PurchasedItemAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItemWithPost> purchasedItems;

    public PurchasedItemAdapter(Context context, List<CartItemWithPost> purchasedItems) {
        this.context = context;
        this.purchasedItems = purchasedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchased, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemWithPost item = purchasedItems.get(position);

        holder.tvPostTitle.setText(item.getTitle());
        holder.tvPostPrice.setText(String.format("Price: %.0f VND", item.getPrice()));

        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.ivPostImage);

        holder.btnDownload.setOnClickListener(v -> {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImagePath());
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return purchasedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImage;
        TextView tvPostTitle, tvPostPrice;
        Button btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPostPrice = itemView.findViewById(R.id.tvPostPrice);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}