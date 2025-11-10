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

    // ===== Callback types cho async =====

    public interface PostsCallback {
        void onResult(List<Post> posts);
    }

    public interface PostCallback {
        void onResult(Post post);
    }


    public long insertPost(Post post) {
        return postDao.insert(post);
    }

    public void updatePost(Post post) {
        postDao.update(post);
    }

    public void deletePost(int postId) {
        postDao.deletePost(postId);
    }

    public Post getPostById(int postId) {
        return postDao.getPostById(postId);
    }

    public List<Post> getAllPostsSync() {
        return postDao.getAllPosts();
    }

    public List<Post> getPostsByUserSync(int userId) {
        return postDao.getPostsByUser(userId);
    }

    // Helper tạo Post mới (validate cơ bản)
    public long createPost(String title,
                           String description,
                           String imagePath,
                           double price,
                           boolean isMature,
                           boolean isPortfolio,
                           int userId) {

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL is required");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        Post post = new Post();
        post.setTitle(title.trim());
        post.setDescription(description != null ? description.trim() : null);
        post.setImagePath(imagePath.trim());
        post.setPrice(price);
        post.setMature(isMature);
        post.setPortfolio(isPortfolio);
        post.setUserId(userId);
        post.setPostDate(new Date());

        return postDao.insert(post);
    }


    public PostWithDetails getPostWithDetails(int postId) {
        return postDao.getPostWithDetails(postId);
    }


    public void likePost(int postId) {
        executorService.execute(() -> postDao.incrementLikeCount(postId));
    }


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

    public void getPostByIdAsync(int postId, PostCallback callback) {
        executorService.execute(() -> {
            Post post = postDao.getPostById(postId);
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(post));
            }
        });
    }
}
