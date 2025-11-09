package com.example.gallerycart.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerycart.R;
import com.example.gallerycart.data.entity.Post;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    private final List<Post> posts = new ArrayList<>();
    private final OnPostClickListener listener;

    public PostAdapter(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<Post> newPosts) {
        posts.clear();
        if (newPosts != null) posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.tvPostTitle.setText(post.getTitle() != null ? post.getTitle() : "Untitled");

        double price = post.getPrice();
        if (price <= 0) holder.tvPostPrice.setText("Free");
        else {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            holder.tvPostPrice.setText(format.format(price));
        }

        holder.tvPostLikes.setText(post.getLikeCount() + " likes");

        boolean isMature = post.isMature();
        if (isMature) {
            holder.tvPostBadge.setVisibility(View.VISIBLE);
            holder.tvPostBadge.setText("Mature");
            holder.ivPostImage.setAlpha(0.5f);
        } else if (price > 0) {
            holder.tvPostBadge.setVisibility(View.GONE);
            holder.ivPostImage.setAlpha(0.8f);
        } else {
            holder.tvPostBadge.setVisibility(View.GONE);
            holder.ivPostImage.setAlpha(1.0f);
        }

        String url = post.getImagePath();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(holder.ivPostImage.getContext())
                    .load(url)
                    .placeholder(R.drawable.defaultavatar)
                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setImageResource(R.drawable.defaultavatar);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPostImage;
        TextView tvPostTitle;
        TextView tvPostPrice;
        TextView tvPostLikes;
        TextView tvPostBadge;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPostPrice = itemView.findViewById(R.id.tvPostPrice);
            tvPostLikes = itemView.findViewById(R.id.tvPostLikes);
            tvPostBadge = itemView.findViewById(R.id.tvPostBadge);
        }
    }
}
