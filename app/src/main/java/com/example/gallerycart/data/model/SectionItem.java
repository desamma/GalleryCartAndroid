package com.example.gallerycart.data.model;

import androidx.annotation.Nullable;

public class SectionItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    public final int type;

    public final int cartId;
    public final long purchaseDateMillis;
    public final double cartTotal;

    @Nullable
    public final CartItemWithPost item;

    public SectionItem(int cartId, long purchaseDateMillis, double cartTotal) {
        this.type = TYPE_HEADER;
        this.cartId = cartId;
        this.purchaseDateMillis = purchaseDateMillis;
        this.cartTotal = cartTotal;
        this.item = null;
    }

    public SectionItem(CartItemWithPost item) {
        this.type = TYPE_ITEM;
        this.cartId = item.getCartItem() != null ? item.getCartItem().getCartId() : -1;
        this.purchaseDateMillis = -1;
        this.cartTotal = 0;
        this.item = item;
    }
}

