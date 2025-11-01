package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gallerycart.data.entity.PostTag;

@Dao
public interface PostTagDao {
    @Insert
    void insert(PostTag postTag);

    @Query("DELETE FROM post_tag WHERE postId = :postId AND tagId = :tagId")
    void removeTagFromPost(int postId, int tagId);

    @Query("DELETE FROM post_tag WHERE postId = :postId")
    void removeAllTagsFromPost(int postId);
}