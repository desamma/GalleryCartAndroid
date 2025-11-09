package com.example.gallerycart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        if (newPosts != null) {
            posts.addAll(newPosts);
        }
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

        // Format giá: nếu sau này cho phép 0 => "Free"
        double price = post.getPrice();
        if (price <= 0) {
            holder.tvPostPrice.setText("Free");
        } else {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            holder.tvPostPrice.setText(format.format(price));
        }

        holder.tvPostLikes.setText(post.getLikeCount() + " likes");

        // Badge & hiệu ứng mờ theo isMature / price
        boolean isMature = post.isMature();
        if (isMature) {
            holder.tvPostBadge.setVisibility(View.VISIBLE);
            holder.tvPostBadge.setText("Mature");
            holder.ivPostImage.setAlpha(0.5f); // ~ mờ 50%
        } else if (price > 0) {
            holder.tvPostBadge.setVisibility(View.GONE);
            holder.ivPostImage.setAlpha(0.8f); // ~ mờ 20%
        } else {
            holder.tvPostBadge.setVisibility(View.GONE);
            holder.ivPostImage.setAlpha(1.0f);
        }

        // Hiện tại chưa load ảnh Cloudinary: dùng placeholder defaultavatar
        // Sau này có URL -> chỉ cần thay bằng thư viện load ảnh (Glide/Picasso/...)
        holder.ivPostImage.setImageResource(R.drawable.defaultavatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
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
