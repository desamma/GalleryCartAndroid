package com.example.gallerycart.repository;

import android.content.Context;

import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CommentDao;
import com.example.gallerycart.data.entity.Comment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentRepository {
    private final CommentDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface CommentsCallback { void onResult(List<Comment> comments); }
    public interface IdCallback { void onResult(long id); }

    public CommentRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).commentDao();
    }

    public void addCommentAsync(Comment c, IdCallback cb) {
        executor.execute(() -> {
            long id = dao.insert(c);
            if (cb != null) cb.onResult(id);
        });
    }

    public void getCommentsByPostAsync(int postId, CommentsCallback cb) {
        executor.execute(() -> {
            List<Comment> list = dao.getCommentsByPost(postId);
            if (cb != null) cb.onResult(list);
        });
    }
}
