package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CartDao;
import com.example.gallerycart.data.dao.CartItemDao;
import com.example.gallerycart.data.dao.PostDao;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.entity.CartItem;
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

    public Cart getOrCreateActiveCart(int userId) {
        Cart cart = cartDao.getActiveCartByUser(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setTotalPrice(0.0);
            cart.setActive(true);
            cart.setCreatedDate(new Date());
            long cartId = cartDao.insert(cart);
            cart.setId((int) cartId);
        }
        return cart;
    }

    public List<Cart> getAllCartsByUser(int userId) {
        return cartDao.getAllCartsByUser(userId);
    }

    public List<Cart> getOrderHistory(int userId) {
        return cartDao.getPurchasedCartsByUser(userId);
    }

    public Cart createNewCart(int userId) {
        // Deactivate all existing active carts
        cartDao.deactivateAllUserCarts(userId);

        // Create new active cart
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalPrice(0.0);
        cart.setActive(true);
        cart.setCreatedDate(new Date());
        long cartId = cartDao.insert(cart);
        cart.setId((int) cartId);

        return cart;
    }

    public long addToCart(int userId, int postId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateActiveCart(userId);

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

    public List<CartItemWithPost> getCartItemsWithPosts(int userId) {
        Cart cart = cartDao.getActiveCartByUser(userId);
        if (cart == null) {
            return null;
        }
        return cartItemDao.getCartItemsWithPosts(cart.getId());
    }

    public List<CartItemWithPost> getCartItemsById(int cartId) {
        return cartItemDao.getCartItemsWithPosts(cartId);
    }

    private void updateCartTotal(int cartId) {
        List<CartItemWithPost> items = cartItemDao.getCartItemsWithPosts(cartId);
        double total = 0.0;
        for (CartItemWithPost item : items) {
            total += item.getPrice() * item.getCartItem().getQuantity();
        }
        cartDao.updateTotalPrice(cartId, total);
    }

    public void checkoutCart(int userId) {
        Cart cart = cartDao.getActiveCartByUser(userId);
        if (cart == null) {
            throw new IllegalStateException("No active cart found for user");
        }

        List<CartItemWithPost> items = cartItemDao.getCartItemsWithPosts(cart.getId());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        cartDao.setPurchaseDate(cart.getId(), new Date().getTime());

        for (CartItemWithPost item : items) {
            postDao.incrementSaleCount(item.getCartItem().getPostId());
        }

        createNewCart(userId);
    }
}