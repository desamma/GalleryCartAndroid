package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "favourite_post",
        primaryKeys = {"postId", "userId"},
        foreignKeys = {
                @ForeignKey(entity = Post.class,
                        parentColumns = "id",
                        childColumns = "postId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("postId"), @Index("userId")})
public class FavouritePost {
    private int postId;
    private int userId;

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}