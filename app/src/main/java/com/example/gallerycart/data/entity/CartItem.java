package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_item",
        foreignKeys = {
                @ForeignKey(entity = Cart.class,
                        parentColumns = "id",
                        childColumns = "cartId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Post.class,
                        parentColumns = "id",
                        childColumns = "postId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("cartId"), @Index("postId")})
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int cartId;
    private int postId;
    private int quantity;

    public CartItem() {
        this.quantity = 1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}