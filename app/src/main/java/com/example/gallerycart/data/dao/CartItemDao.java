package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.CartItem;
import com.example.gallerycart.data.model.CartItemWithPost;
import java.util.List;

@Dao
public interface CartItemDao {
    @Insert
    long insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);

    @Query("SELECT ci.*, p.title, p.imagePath, p.price " +
            "FROM cart_item ci " +
            "INNER JOIN post p ON ci.postId = p.id " +
            "WHERE ci.cartId = :cartId")
    List<CartItemWithPost> getCartItemsWithPosts(int cartId);

    @Query("SELECT * FROM cart_item WHERE cartId = :cartId AND postId = :postId LIMIT 1")
    CartItem getCartItem(int cartId, int postId);

    @Query("DELETE FROM cart_item WHERE id = :cartItemId")
    void deleteCartItem(int cartItemId);

    @Query("DELETE FROM cart_item WHERE cartId = :cartId")
    void clearCart(int cartId);
}