package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.data.model.PostWithDetails;
import java.util.List;

@Dao
public interface PostDao {
    @Insert
    long insert(Post post);

    @Update
    void update(Post post);

    @Query("SELECT * FROM post WHERE id = :postId")
    Post getPostById(int postId);

    @Query("SELECT * FROM post WHERE userId = :userId ORDER BY postDate DESC")
    List<Post> getPostsByUser(int userId);

    @Query("SELECT * FROM post WHERE isPortfolio = 1 AND userId = :userId")
    List<Post> getPortfolioByUser(int userId);

    //nên dùng hay ko?
    @Query("SELECT p.*, " +
            "(SELECT COUNT(*) FROM comment WHERE postId = p.id) as commentCount " +
            "FROM post p WHERE p.id = :postId")
    PostWithDetails getPostWithDetails(int postId);

    @Query("SELECT * FROM post ORDER BY postDate DESC LIMIT :limit")
    List<Post> getRecentPosts(int limit);

    @Query("UPDATE post SET likeCount = likeCount + 1 WHERE id = :postId")
    void incrementLikeCount(int postId);

    @Query("UPDATE post SET saleCount = saleCount + 1 WHERE id = :postId")
    void incrementSaleCount(int postId);

    @Query("DELETE FROM post WHERE id = :postId")
    void deletePost(int postId);
}