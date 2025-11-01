package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CommentDao;
import com.example.gallerycart.data.entity.Comment;
import java.util.Date;
import java.util.List;

public class CommentRepository {

    private final CommentDao commentDao;

    public CommentRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        commentDao = database.commentDao();
    }

    /**
     * Add a comment to a post
     */
    public long addComment(String content, int userId, int postId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setCommentDate(new Date());

        return commentDao.insert(comment);
    }

    /**
     * Get comments for a post
     */
    public List<Comment> getCommentsByPost(int postId) {
        return commentDao.getCommentsByPost(postId);
    }
}