package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.gallerycart.data.entity.FavouritePost;
import com.example.gallerycart.data.entity.Post;

import java.util.List;

@Dao
public interface FavouritePostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FavouritePost favouritePost);

    @Query("DELETE FROM favourite_post WHERE postId = :postId AND userId = :userId")
    void delete(int postId, int userId);

    @Query("SELECT COUNT(*) FROM favourite_post WHERE postId = :postId AND userId = :userId")
    int isFavourited(int postId, int userId);

    @Query("SELECT p.* FROM post p INNER JOIN favourite_post f ON p.id = f.postId WHERE f.userId = :userId ORDER BY p.title COLLATE NOCASE ASC")
    List<Post> getFavouritesByUser(int userId);
}
