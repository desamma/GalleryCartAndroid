package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CartDao;
import com.example.gallerycart.data.dao.MomoPaymentDao;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.entity.MomoPayment;
import java.util.Date;
import java.util.UUID;

public class MomoPaymentRepository {

    private final MomoPaymentDao momoPaymentDao;
    private final CartDao cartDao;

    public MomoPaymentRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        momoPaymentDao = database.momoPaymentDao();
        cartDao = database.cartDao();
    }

    /**
     * Create Momo payment for cart
     */
    public long createMomoPayment(int cartId, String partnerCode, double amount) {
        Cart cart = cartDao.getCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        MomoPayment payment = new MomoPayment();
        payment.setCartId(cartId);
        payment.setPartnerCode(partnerCode);
        payment.setRequestId(UUID.randomUUID().toString());
        payment.setOrderId("ORDER_" + System.currentTimeMillis());
        payment.setAmount(amount);
        payment.setOrderInfo("Thanh toán qua ví momo");
        payment.setRequestType("captureWallet");
        payment.setCreatedAt(new Date());
        payment.setUpdatedAt(new Date());

        return momoPaymentDao.insert(payment);
    }

    /**
     * Update payment status (e.g., after callback from Momo)
     */
    public void updatePaymentStatus(int paymentId, int resultCode, String message) {
        momoPaymentDao.updatePaymentStatus(paymentId, resultCode, message, new Date().getTime());
    }

    /**
     * Get payment by cart ID
     */
    public MomoPayment getPaymentByCart(int cartId) {
        return momoPaymentDao.getPaymentByCart(cartId);
    }
}