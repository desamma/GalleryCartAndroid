package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "momo_payment",
        foreignKeys = @ForeignKey(entity = Cart.class,
                parentColumns = "id",
                childColumns = "cartId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "cartId", unique = true)})
public class MomoPayment {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int cartId;
    private String partnerCode;
    private String requestId;
    private String orderId;
    private double amount;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private String signature;
    private String payUrl;
    private String deepLink;
    private String qrCodeUrl;
    private int resultCode;
    private String message;
    private long responseTime;
    private Date createdAt;
    private Date updatedAt;

    public MomoPayment() {
        this.requestType = "captureWallet";
        this.resultCode = -1;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public String getPartnerCode() { return partnerCode; }
    public void setPartnerCode(String partnerCode) { this.partnerCode = partnerCode; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public String getIpnUrl() { return ipnUrl; }
    public void setIpnUrl(String ipnUrl) { this.ipnUrl = ipnUrl; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }

    public String getDeepLink() { return deepLink; }
    public void setDeepLink(String deepLink) { this.deepLink = deepLink; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public int getResultCode() { return resultCode; }
    public void setResultCode(int resultCode) { this.resultCode = resultCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}