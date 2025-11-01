package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "post",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String imagePath;
    private Date postDate;
    private int likeCount;
    private int saleCount;
    private boolean isPortfolio;
    private boolean isMature;
    private double price;
    private int userId;

    public Post() {
        this.postDate = new Date();
        this.likeCount = 0;
        this.saleCount = 0;
        this.isPortfolio = false;
        this.isMature = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getSaleCount() { return saleCount; }
    public void setSaleCount(int saleCount) { this.saleCount = saleCount; }

    public boolean isPortfolio() { return isPortfolio; }
    public void setPortfolio(boolean portfolio) { isPortfolio = portfolio; }

    public boolean isMature() { return isMature; }
    public void setMature(boolean mature) { isMature = mature; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}