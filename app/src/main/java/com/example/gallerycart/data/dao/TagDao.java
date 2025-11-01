package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gallerycart.data.entity.Tag;
import java.util.List;

@Dao
public interface TagDao {
    @Insert
    long insert(Tag tag);

    @Query("SELECT * FROM tag WHERE id = :tagId")
    Tag getTagById(int tagId);

    @Query("SELECT * FROM tag WHERE tagName = :tagName LIMIT 1")
    Tag getTagByName(String tagName);

    @Query("SELECT t.* FROM tag t " +
            "INNER JOIN post_tag pt ON t.id = pt.tagId " +
            "WHERE pt.postId = :postId")
    List<Tag> getTagsByPost(int postId);

    @Query("SELECT * FROM tag")
    List<Tag> getAllTags();
}