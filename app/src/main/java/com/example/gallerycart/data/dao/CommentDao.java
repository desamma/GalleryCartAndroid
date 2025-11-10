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

    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY commentDate ASC")
    List<Comment> getCommentsByPost(int postId);
}
