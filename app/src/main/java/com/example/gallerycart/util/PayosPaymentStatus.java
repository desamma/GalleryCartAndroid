package com.example.gallerycart.util;

public class PayosPaymentStatus {
    public static final String PENDING = "PENDING";
    public static final String CANCELLED = "CANCELLED";
    public static final String UNDERPAID = "UNDERPAID";
    public static final String PAID = "PAID";
    public static final String EXPIRED = "EXPIRED";
    public static final String PROCESSING = "PROCESSING";
    public static final String FAILED = "FAILED";

    public static boolean isFinalStatus(String status) {
        return PAID.equals(status) ||
                CANCELLED.equals(status) ||
                EXPIRED.equals(status) ||
                FAILED.equals(status);
    }

    public static boolean isSuccessful(String status) {
        return PAID.equals(status);
    }

    public static boolean isPending(String status) {
        return PENDING.equals(status) ||
                PROCESSING.equals(status) ||
                UNDERPAID.equals(status);
    }
}