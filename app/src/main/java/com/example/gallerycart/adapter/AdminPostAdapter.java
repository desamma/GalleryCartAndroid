package com.example.gallerycart.adapter;

import android.content.Context;
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
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.PostViewHolder> {

    private final Context context;
    private List<Post> posts = new ArrayList<>();
    private List<Post> postsFiltered = new ArrayList<>();
    private OnPostActionListener listener;
    private String currentSortBy = "date_desc";

    public interface OnPostActionListener {
        void onViewPost(Post post);
        void onDeletePost(Post post);
        String getArtistName(int userId);
    }

    public AdminPostAdapter(Context context) {
        this.context = context;
    }

    public void setOnPostActionListener(OnPostActionListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts != null ? posts : new ArrayList<>();
        this.postsFiltered = new ArrayList<>(this.posts);
        applySorting();
        notifyDataSetChanged();
    }

    public void filter(String query) {
        postsFiltered.clear();
        if (query == null || query.isEmpty()) {
            postsFiltered.addAll(posts);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Post post : posts) {
                if (post.getTitle().toLowerCase().contains(lowerQuery) ||
                        (post.getDescription() != null && post.getDescription().toLowerCase().contains(lowerQuery))) {
                    postsFiltered.add(post);
                }
            }
        }
        applySorting();
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        postsFiltered.clear();
        switch (type) {
            case "all":
                postsFiltered.addAll(posts);
                break;
            case "portfolio":
                for (Post post : posts) {
                    if (post.isPortfolio()) {
                        postsFiltered.add(post);
                    }
                }
                break;
            case "mature":
                for (Post post : posts) {
                    if (post.isMature()) {
                        postsFiltered.add(post);
                    }
                }
                break;
        }
        applySorting();
        notifyDataSetChanged();
    }

    public void sortBy(String sortBy) {
        this.currentSortBy = sortBy;
        applySorting();
        notifyDataSetChanged();
    }

    private void applySorting() {
        switch (currentSortBy) {
            case "date_desc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        p2.getPostDate().compareTo(p1.getPostDate()));
                break;
            case "date_asc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        p1.getPostDate().compareTo(p2.getPostDate()));
                break;
            case "likes_desc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        Integer.compare(p2.getLikeCount(), p1.getLikeCount()));
                break;
            case "sales_desc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        Integer.compare(p2.getSaleCount(), p1.getSaleCount()));
                break;
            case "price_desc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "price_asc":
                Collections.sort(postsFiltered, (p1, p2) ->
                        Double.compare(p1.getPrice(), p2.getPrice()));
                break;
        }
    }

    public int getFilteredCount() {
        return postsFiltered.size();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postsFiltered.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postsFiltered.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPostImage;
        private final TextView tvTitle;
        private final TextView tvArtist;
        private final TextView tvLikes;
        private final TextView tvSales;
        private final TextView tvPrice;
        private final TextView tvPortfolioBadge;
        private final TextView tvMatureBadge;
        private final MaterialButton btnView;
        private final MaterialButton btnDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSales = itemView.findViewById(R.id.tvSales);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPortfolioBadge = itemView.findViewById(R.id.tvPortfolioBadge);
            tvMatureBadge = itemView.findViewById(R.id.tvMatureBadge);
            btnView = itemView.findViewById(R.id.btnView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Post post) {
            tvTitle.setText(post.getTitle());

            if (listener != null) {
                String artistName = listener.getArtistName(post.getUserId());
                tvArtist.setText("by " + (artistName != null ? artistName : "Unknown Artist"));
            }

            tvLikes.setText("❤️ " + post.getLikeCount());
            tvSales.setText("🛒 " + post.getSaleCount());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(formatter.format(post.getPrice()));

            tvPortfolioBadge.setVisibility(post.isPortfolio() ? View.VISIBLE : View.GONE);
            tvMatureBadge.setVisibility(post.isMature() ? View.VISIBLE : View.GONE);

            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewPost(post);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeletePost(post);
                }
            });

            Glide.with(context).load(post.getImagePath()).into(ivPostImage);
        }
    }
}
