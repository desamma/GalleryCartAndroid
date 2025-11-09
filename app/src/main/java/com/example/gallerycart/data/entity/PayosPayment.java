package com.example.gallerycart.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.gallerycart.data.Converters;
import java.util.Date;
import java.util.List;

@Entity(tableName = "payos_payment",
        foreignKeys = @ForeignKey(entity = Cart.class,
                parentColumns = "id",
                childColumns = "cartId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "cartId", unique = true), @Index("orderCode")})
public class PayosPayment {

    @PrimaryKey
    @NonNull
    private String id;

    private int cartId;
    private long orderCode;
    private long amount;
    private long amountPaid;
    private long amountRemaining;
    private String status; // PENDING, CANCELLED, UNDERPAID, PAID, EXPIRED, PROCESSING, FAILED
    private Date createdAt;
    private Date canceledAt;
    private String cancellationReason;

    private String transactionsJson;

    public PayosPayment() {
        this.createdAt = new Date();
        this.status = "PENDING";
        this.amountPaid = 0;
        this.id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public long getOrderCode() { return orderCode; }
    public void setOrderCode(long orderCode) { this.orderCode = orderCode; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public long getAmountPaid() { return amountPaid; }
    public void setAmountPaid(long amountPaid) { this.amountPaid = amountPaid; }

    public long getAmountRemaining() { return amountRemaining; }
    public void setAmountRemaining(long amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getCanceledAt() { return canceledAt; }
    public void setCanceledAt(Date canceledAt) { this.canceledAt = canceledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getTransactionsJson() { return transactionsJson; }
    public void setTransactionsJson(String transactionsJson) {
        this.transactionsJson = transactionsJson;
    }
}