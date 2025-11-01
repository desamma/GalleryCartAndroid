package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gallerycart.data.entity.Comment;
import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    long insert(Comment comment);

    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY commentDate DESC")
    List<Comment> getCommentsByPost(int postId);

    @Query("SELECT * FROM comment WHERE userId = :userId ORDER BY commentDate DESC")
    List<Comment> getCommentsByUser(int userId);

    @Query("DELETE FROM comment WHERE id = :commentId")
    void deleteComment(int commentId);
}