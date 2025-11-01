package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CartDao;
import com.example.gallerycart.data.dao.CartItemDao;
import com.example.gallerycart.data.dao.PostDao;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.entity.CartItem;
import com.example.gallerycart.data.entity.Post;
import com.example.gallerycart.data.model.CartItemWithPost;
import java.util.Date;
import java.util.List;

public class CartRepository {

    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final PostDao postDao;

    public CartRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        cartDao = database.cartDao();
        cartItemDao = database.cartItemDao();
        postDao = database.postDao();
    }

    /**
     * Get or create cart for user
     */
    public Cart getOrCreateCart(int userId) {
        Cart cart = cartDao.getCartByUser(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setTotalPrice(0.0);
            long cartId = cartDao.insert(cart);
            cart.setId((int) cartId);
        }
        return cart;
    }

    /**
     * Add item to cart
     */
    public long addToCart(int userId, int postId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(userId);

        // Check if item already exists in cart
        CartItem existingItem = cartItemDao.getCartItem(cart.getId(), postId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemDao.update(existingItem);
            updateCartTotal(cart.getId());
            return existingItem.getId();
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setPostId(postId);
            cartItem.setQuantity(quantity);
            long itemId = cartItemDao.insert(cartItem);
            updateCartTotal(cart.getId());
            return itemId;
        }
    }

    /**
     * Get cart items with post details
     */
    public List<CartItemWithPost> getCartItemsWithPosts(int userId) {
        Cart cart = cartDao.getCartByUser(userId);
        if (cart == null) {
            return null;
        }
        return cartItemDao.getCartItemsWithPosts(cart.getId());
    }

    /**
     * Update cart total price
     */
    private void updateCartTotal(int cartId) {
        List<CartItemWithPost> items = cartItemDao.getCartItemsWithPosts(cartId);
        double total = 0.0;
        for (CartItemWithPost item : items) {
            total += item.getPrice() * item.getCartItem().getQuantity();
        }
        cartDao.updateTotalPrice(cartId, total);
    }

    /**
     * Checkout cart
     */
    public void checkoutCart(int userId) {
        Cart cart = cartDao.getCartByUser(userId);
        if (cart == null) {
            throw new IllegalStateException("No cart found for user");
        }

        List<CartItemWithPost> items = cartItemDao.getCartItemsWithPosts(cart.getId());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Update purchase date
        cartDao.setPurchaseDate(cart.getId(), new Date().getTime());

        // Increment sale count for each post
        for (CartItemWithPost item : items) {
            postDao.incrementSaleCount(item.getCartItem().getPostId());
        }
    }
}