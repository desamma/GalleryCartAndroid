package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.model.CartWithItems;

@Dao
public interface CartDao {
    @Insert
    long insert(Cart cart);

    @Update
    void update(Cart cart);

    @Query("SELECT * FROM cart WHERE userId = :userId LIMIT 1")
    Cart getCartByUser(int userId);

    @Query("SELECT * FROM cart WHERE id = :cartId")
    Cart getCartById(int cartId);

    @Query("SELECT c.*, " +
            "(SELECT COUNT(*) FROM cart_item WHERE cartId = c.id) as itemCount " +
            "FROM cart c WHERE c.userId = :userId")
    CartWithItems getCartWithItemsByUser(int userId);

    @Query("UPDATE cart SET totalPrice = :totalPrice WHERE id = :cartId")
    void updateTotalPrice(int cartId, double totalPrice);

    @Query("UPDATE cart SET purchaseDate = :purchaseDate WHERE id = :cartId")
    void setPurchaseDate(int cartId, long purchaseDate);
}