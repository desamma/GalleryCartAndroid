package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gallerycart.data.entity.FavouritePost;
import com.example.gallerycart.data.entity.Post;
import java.util.List;

@Dao
public interface FavouritePostDao {
    @Insert
    void insert(FavouritePost favouritePost);

    @Query("SELECT p.* FROM post p " +
            "INNER JOIN favourite_post fp ON p.id = fp.postId " +
            "WHERE fp.userId = :userId")
    List<Post> getFavouritePostsByUser(int userId);

    @Query("DELETE FROM favourite_post WHERE postId = :postId AND userId = :userId")
    void removeFavourite(int postId, int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_post " +
            "WHERE postId = :postId AND userId = :userId)")
    boolean isFavourite(int postId, int userId);
}