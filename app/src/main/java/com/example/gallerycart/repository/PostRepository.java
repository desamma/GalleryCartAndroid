package com.example.gallerycart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.PostDao;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.data.model.PostWithDetails;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostRepository {

    private final PostDao postDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public PostRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        postDao = database.postDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface PostsCallback {
        void onResult(List<Post> posts);
    }

    /**
     * Create a new post
     */
    public long createPost(String title, String imagePath, double price, int userId) {
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Image path is required");
        }
        if (price <= 0) {
            // Hiện tại DB + trigger đang yêu cầu price > 0
            // Nếu muốn hỗ trợ Free (0), cần sửa DB/migration sau.
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        Post post = new Post();
        post.setTitle(title);
        post.setImagePath(imagePath);
        post.setPrice(price);
        post.setUserId(userId);
        post.setPostDate(new Date());

        return postDao.insert(post);
    }

    /**
     * Get posts by user (sync) - NÊN gọi từ background thread
     */
    public List<Post> getPostsByUser(int userId) {
        return postDao.getPostsByUser(userId);
    }

    /**
     * Get post with details (including comment count)
     */
    public PostWithDetails getPostWithDetails(int postId) {
        return postDao.getPostWithDetails(postId);
    }

    /**
     * Increment like count
     */
    public void likePost(int postId) {
        executorService.execute(() -> postDao.incrementLikeCount(postId));
    }

    // ========== MỚI: API cho HomePage ==========

    public void getTopLikedPostsAsync(int limit, PostsCallback callback) {
        executorService.execute(() -> {
            List<Post> result = postDao.getTopLikedPosts(limit);
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(result));
            }
        });
    }

    public void getRandomPostsAsync(int limit, PostsCallback callback) {
        executorService.execute(() -> {
            List<Post> result = postDao.getRandomPosts(limit);
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(result));
            }
        });
    }

    public void getRecentPostsAsync(int limit, PostsCallback callback) {
        executorService.execute(() -> {
            List<Post> result = postDao.getRecentPosts(limit);
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(result));
            }
        });
    }

    public void getAllPostsAsync(PostsCallback callback) {
        executorService.execute(() -> {
            List<Post> result = postDao.getAllPosts();
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(result));
            }
        });
    }

    // ==========================================
}
