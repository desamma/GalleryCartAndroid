package com.example.gallerycart.data.model;

import androidx.room.Embedded;
import com.example.gallerycart.data.entity.CartItem;

public class CartItemWithPost {
    @Embedded
    public CartItem cartItem;

    public String title;
    public String imagePath;
    public double price;

    public CartItem getCartItem() { return cartItem; }
    public void setCartItem(CartItem cartItem) { this.cartItem = cartItem; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}