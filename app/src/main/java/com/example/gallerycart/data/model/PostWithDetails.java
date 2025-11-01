package com.example.gallerycart.data.model;

import androidx.room.Embedded;
import com.example.gallerycart.data.entity.Post;

public class PostWithDetails {
    @Embedded
    public Post post;

    public int commentCount;

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
}