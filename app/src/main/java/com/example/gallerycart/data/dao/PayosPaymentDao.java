package com.example.gallerycart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.PayosPayment;
import java.util.List;

@Dao
public interface PayosPaymentDao {

    @Insert
    void insert(PayosPayment payment);

    @Update
    void update(PayosPayment payment);

    @Query("SELECT * FROM payos_payment WHERE id = :paymentId")
    PayosPayment getPaymentById(String paymentId);

    @Query("SELECT * FROM payos_payment WHERE cartId = :cartId LIMIT 1")
    PayosPayment getPaymentByCartId(int cartId);

    @Query("SELECT * FROM payos_payment WHERE orderCode = :orderCode LIMIT 1")
    PayosPayment getPaymentByOrderCode(long orderCode);

    @Query("SELECT * FROM payos_payment WHERE status = :status ORDER BY createdAt DESC")
    List<PayosPayment> getPaymentsByStatus(String status);

    @Query("SELECT pp.* FROM payos_payment pp " +
            "INNER JOIN cart c ON pp.cartId = c.id " +
            "WHERE c.userId = :userId ORDER BY pp.createdAt DESC")
    List<PayosPayment> getPaymentsByUserId(int userId);

    @Query("SELECT * FROM payos_payment WHERE status = 'PAID' ORDER BY createdAt DESC")
    List<PayosPayment> getSuccessfulPayments();

    @Query("SELECT * FROM payos_payment WHERE status = 'PENDING' ORDER BY createdAt DESC")
    List<PayosPayment> getPendingPayments();

    @Query("UPDATE payos_payment SET status = :status, amountPaid = :amountPaid, " +
            "amountRemaining = :amountRemaining WHERE id = :paymentId")
    void updatePaymentStatus(String paymentId, String status, long amountPaid, long amountRemaining);

    @Query("UPDATE payos_payment SET status = :status, canceledAt = :canceledAt, " +
            "cancellationReason = :reason WHERE id = :paymentId")
    void cancelPayment(String paymentId, String status, long canceledAt, String reason);

    @Query("UPDATE payos_payment SET transactionsJson = :transactionsJson WHERE id = :paymentId")
    void updateTransactions(String paymentId, String transactionsJson);

    @Query("DELETE FROM payos_payment WHERE id = :paymentId")
    void deletePayment(String paymentId);
}