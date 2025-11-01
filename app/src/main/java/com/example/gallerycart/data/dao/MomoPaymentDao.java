package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.MomoPayment;
import java.util.List;

@Dao
public interface MomoPaymentDao {
    @Insert
    long insert(MomoPayment momoPayment);

    @Update
    void update(MomoPayment momoPayment);

    @Query("SELECT * FROM momo_payment WHERE id = :paymentId")
    MomoPayment getPaymentById(int paymentId);

    @Query("SELECT * FROM momo_payment WHERE cartId = :cartId LIMIT 1")
    MomoPayment getPaymentByCart(int cartId);

    @Query("SELECT * FROM momo_payment WHERE orderId = :orderId LIMIT 1")
    MomoPayment getPaymentByOrderId(String orderId);

    @Query("SELECT * FROM momo_payment WHERE resultCode = 0 ORDER BY createdAt DESC")
    List<MomoPayment> getSuccessfulPayments();

    @Query("UPDATE momo_payment SET resultCode = :resultCode, message = :message, " +
            "updatedAt = :updatedAt WHERE id = :paymentId")
    void updatePaymentStatus(int paymentId, int resultCode, String message, long updatedAt);
}