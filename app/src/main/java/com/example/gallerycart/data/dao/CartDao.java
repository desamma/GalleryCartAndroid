package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.model.CartWithItems;
import java.util.List;

@Dao
public interface CartDao {
    @Insert
    long insert(Cart cart);

    @Update
    void update(Cart cart);

    @Query("SELECT * FROM cart WHERE userId = :userId AND isActive = 1 " +
            "ORDER BY createdDate DESC LIMIT 1")
    Cart getActiveCartByUser(int userId);

    @Query("SELECT * FROM cart WHERE userId = :userId ORDER BY createdDate DESC")
    List<Cart> getAllCartsByUser(int userId);

    @Query("SELECT * FROM cart WHERE userId = :userId AND purchaseDate IS NOT NULL " +
            "ORDER BY purchaseDate DESC")
    List<Cart> getPurchasedCartsByUser(int userId);

    @Query("SELECT * FROM cart WHERE id = :cartId")
    Cart getCartById(int cartId);

    @Query("SELECT c.*, " +
            "(SELECT COUNT(*) FROM cart_item WHERE cartId = c.id) as itemCount " +
            "FROM cart c WHERE c.id = :cartId")
    CartWithItems getCartWithItemsById(int cartId);

    @Query("UPDATE cart SET totalPrice = :totalPrice WHERE id = :cartId")
    void updateTotalPrice(int cartId, double totalPrice);

    @Query("UPDATE cart SET purchaseDate = :purchaseDate, isActive = 0 WHERE id = :cartId")
    void setPurchaseDate(int cartId, long purchaseDate);

    @Query("UPDATE cart SET isActive = 0 WHERE userId = :userId")
    void deactivateAllUserCarts(int userId);

    @Query("UPDATE cart SET isActive = 1 WHERE id = :cartId")
    void setCartActive(int cartId);

    @Query("DELETE FROM cart WHERE id = :cartId")
    void deleteCart(int cartId);
}