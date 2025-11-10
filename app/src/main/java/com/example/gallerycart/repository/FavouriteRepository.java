package com.example.gallerycart.repository;

import android.content.Context;

import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.FavouritePostDao;
import com.example.gallerycart.data.entity.FavouritePost;
import com.example.gallerycart.data.entity.Post;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouriteRepository {
    private final FavouritePostDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface BooleanCallback { void onResult(boolean value); }
    public interface PostsCallback { void onResult(List<Post> posts); }
    public interface VoidCallback { void onDone(); }

    public FavouriteRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).favouritePostDao();
    }

    public void isFavouritedAsync(int postId, int userId, BooleanCallback cb) {
        executor.execute(() -> {
            boolean fav = dao.isFavourited(postId, userId) > 0;
            if (cb != null) cb.onResult(fav);
        });
    }

    public void addFavouriteAsync(int postId, int userId, VoidCallback cb) {
        executor.execute(() -> {
            FavouritePost f = new FavouritePost();
            f.setPostId(postId);
            f.setUserId(userId);
            dao.insert(f);
            if (cb != null) cb.onDone();
        });
    }

    public void removeFavouriteAsync(int postId, int userId, VoidCallback cb) {
        executor.execute(() -> {
            dao.delete(postId, userId);
            if (cb != null) cb.onDone();
        });
    }

    public void getFavouritesByUserAsync(int userId, PostsCallback cb) {
        executor.execute(() -> {
            List<Post> list = dao.getFavouritesByUser(userId);
            if (cb != null) cb.onResult(list);
        });
    }
}
