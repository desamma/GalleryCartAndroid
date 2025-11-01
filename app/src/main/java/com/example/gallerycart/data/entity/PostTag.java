package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "post_tag",
        primaryKeys = {"postId", "tagId"},
        foreignKeys = {
                @ForeignKey(entity = Post.class,
                        parentColumns = "id",
                        childColumns = "postId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tag.class,
                        parentColumns = "id",
                        childColumns = "tagId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("postId"), @Index("tagId")})
public class PostTag {
    private int postId;
    private int tagId;

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }
}