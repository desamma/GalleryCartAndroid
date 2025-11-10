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
import com.example.gallerycart.data.model.CartWithItems;

import java.util.Date;
import java.util.List;

public class CartRepository {

    private final AppDatabase db;
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final PostDao postDao;

    public CartRepository(Context context) {
        db = AppDatabase.getInstance(context.getApplicationContext());
        cartDao = db.cartDao();
        cartItemDao = db.cartItemDao();
        postDao = db.postDao();
    }

    public Cart getOrCreateCart(int userId) {
        Cart cart = cartDao.getActiveCartByUser(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setTotalPrice(0.0);
            cart.setActive(true);
            long cartId = cartDao.insert(cart);
            cart.setId((int) cartId);
        }
        return cart;
    }

    public long addToCart(int userId, int postId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Cart cart = getOrCreateCart(userId);
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

    public Cart getCartById(int cartId) {
        return cartDao.getCartById(cartId);
    }

    public CartWithItems getCartWithItems(int cartId) {
        return cartDao.getCartWithItemsById(cartId);
    }

    public List<CartItemWithPost> getCartItemsWithPosts(int userId) {
        Cart cart = cartDao.getActiveCartByUser(userId);
        if (cart == null) return null;
        return cartItemDao.getCartItemsWithPosts(cart.getId());
    }

    private void updateCartTotal(int cartId) {
        List<CartItemWithPost> cartWithItems = cartDao.getCartItemsWithPosts(cartId);
        if (cartWithItems != null) {
            double total = 0;
            for (CartItemWithPost ciwp : cartWithItems) {
                total += ciwp.getPrice() * ciwp.getCartItem().getQuantity();
            }
            cartDao.updateTotalPrice(cartId, total);
        }
    }

    public void checkoutCart(int userId) {
        db.runInTransaction(() -> {
            Cart cart = cartDao.getActiveCartByUser(userId);
            if (cart == null) throw new IllegalStateException("No active cart to checkout");

            List<CartItemWithPost> cartWithItems = cartDao.getCartItemsWithPosts(cart.getId());
            if (cartWithItems == null || cartWithItems.isEmpty()) {
                throw new IllegalStateException("Cart is empty");
            }

            double total = 0;
            for (CartItemWithPost ciwp : cartWithItems) {
                total += ciwp.getPrice() * ciwp.getCartItem().getQuantity();
                postDao.incrementSaleCount(ciwp.getCartItem().getPostId());
            }

            cartDao.updateTotalPrice(cart.getId(), total);
            long nowMillis = new Date().getTime();
            cartDao.setPurchaseDate(cart.getId(), nowMillis);

            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setTotalPrice(0.0);
            newCart.setActive(true);
            cartDao.insert(newCart);
        });
    }

    public void finalizePurchase(int cartId) {
        db.runInTransaction(() -> {
            Cart cart = cartDao.getCartById(cartId);
            if (cart == null || !cart.isActive()) {
                return;
            }

            List<CartItemWithPost> cartWithItems = cartDao.getCartItemsWithPosts(cart.getId());
            if (cartWithItems != null && !cartWithItems.isEmpty()) {
                for (CartItemWithPost ciwp : cartWithItems) {
                    if (ciwp != null && ciwp.getCartItem() != null) {
                        postDao.incrementSaleCount(ciwp.getCartItem().getPostId());
                    }
                }
            }

            long nowMillis = new Date().getTime();
            cartDao.setPurchaseDate(cart.getId(), nowMillis);

            cartDao.deactivateAllUserCarts(cart.getUserId());
            Cart newCart = new Cart();
            newCart.setUserId(cart.getUserId());
            newCart.setTotalPrice(0.0);
            newCart.setActive(true);
            cartDao.insert(newCart);
        });
    }

    public void updateCartItem(CartItem item) {
        cartItemDao.update(item);
        updateCartTotal(item.getCartId());
    }

    public void removeCartItem(int cartItemId, int cartId) {
        cartItemDao.deleteCartItem(cartItemId);
        updateCartTotal(cartId);
    }

    public double getRevenueBetween(long fromMillis, long toMillis) {
        Double res = cartDao.sumRevenueBetween(fromMillis, toMillis);
        return res == null ? 0.0 : res;
    }

    public List<CartItemWithPost> getSoldItemsBetween(long fromMillis, long toMillis) {
        return cartDao.getSoldItemsBetween(fromMillis, toMillis);
    }
}
