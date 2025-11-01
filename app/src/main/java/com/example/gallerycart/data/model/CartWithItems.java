package com.example.gallerycart.data.model;

import androidx.room.Embedded;
import com.example.gallerycart.data.entity.Cart;

public class CartWithItems {
    @Embedded
    public Cart cart;

    public int itemCount;

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}