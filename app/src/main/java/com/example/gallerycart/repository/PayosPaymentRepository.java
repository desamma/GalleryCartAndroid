package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CartDao;
import com.example.gallerycart.data.dao.PayosPaymentDao;
import com.example.gallerycart.data.entity.Cart;
import com.example.gallerycart.data.entity.PayosPayment;
import com.example.gallerycart.data.model.PayosTransaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PayosPaymentRepository {

    private final PayosPaymentDao payosPaymentDao;
    private final CartDao cartDao;
    private final CartRepository cartRepository;
    private final ExecutorService executorService;
    private final Gson gson;

    public PayosPaymentRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        payosPaymentDao = database.payosPaymentDao();
        cartDao = database.cartDao();
        cartRepository = new CartRepository(context);
        executorService = Executors.newSingleThreadExecutor();
        gson = new Gson();
    }

    public void createPayment(String paymentLinkId, int cartId, long orderCode,
                              long amount, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                Cart cart = cartDao.getCartById(cartId);
                if (cart == null) {
                    if (callback != null) {
                        callback.onError("Cart not found");
                    }
                    return;
                }

                if (amount <= 0) {
                    if (callback != null) {
                        callback.onError("Amount must be greater than 0");
                    }
                    return;
                }

                PayosPayment payment = new PayosPayment();
                payment.setId(paymentLinkId);
                payment.setCartId(cartId);
                payment.setOrderCode(orderCode);
                payment.setAmount(amount);
                payment.setAmountPaid(0);
                payment.setAmountRemaining(amount);
                payment.setStatus("PENDING");
                payment.setCreatedAt(new Date());

                payosPaymentDao.insert(payment);

                if (callback != null) {
                    callback.onSuccess(payment);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to create payment: " + e.getMessage());
                }
            }
        });
    }

    public void updatePaymentStatus(String paymentId, String status, long amountPaid,
                                    long amountRemaining, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                payosPaymentDao.updatePaymentStatus(paymentId, status, amountPaid, amountRemaining);

                PayosPayment payment = payosPaymentDao.getPaymentById(paymentId);
                if (callback != null) {
                    callback.onSuccess(payment);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to update payment status: " + e.getMessage());
                }
            }
        });
    }

    public void cancelPayment(String paymentId, String reason, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                payosPaymentDao.cancelPayment(paymentId, "CANCELLED",
                        new Date().getTime(), reason);

                PayosPayment payment = payosPaymentDao.getPaymentById(paymentId);
                if (callback != null) {
                    callback.onSuccess(payment);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to cancel payment: " + e.getMessage());
                }
            }
        });
    }

    public void addTransaction(String paymentId, PayosTransaction transaction,
                               PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                PayosPayment payment = payosPaymentDao.getPaymentById(paymentId);
                if (payment == null) {
                    if (callback != null) {
                        callback.onError("Payment not found");
                    }
                    return;
                }

                List<PayosTransaction> transactions = parseTransactions(payment.getTransactionsJson());
                transactions.add(transaction);

                String transactionsJson = gson.toJson(transactions);
                payosPaymentDao.updateTransactions(paymentId, transactionsJson);

                long totalPaid = 0;
                for (PayosTransaction t : transactions) {
                    totalPaid += t.getAmount();
                }
                long remaining = payment.getAmount() - totalPaid;

                String newStatus = remaining <= 0 ? "PAID" : "UNDERPAID";
                payosPaymentDao.updatePaymentStatus(paymentId, newStatus, totalPaid, remaining);

                if (newStatus.equals("PAID")) {
                    cartRepository.finalizePurchase(payment.getCartId());
                }

                PayosPayment updatedPayment = payosPaymentDao.getPaymentById(paymentId);
                if (callback != null) {
                    callback.onSuccess(updatedPayment);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to add transaction: " + e.getMessage());
                }
            }
        });
    }

    public void getPaymentById(String paymentId, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                PayosPayment payment = payosPaymentDao.getPaymentById(paymentId);
                if (callback != null) {
                    if (payment != null) {
                        callback.onSuccess(payment);
                    } else {
                        callback.onError("Payment not found");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to get payment: " + e.getMessage());
                }
            }
        });
    }

    public void getPaymentByCartId(int cartId, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                PayosPayment payment = payosPaymentDao.getPaymentByCartId(cartId);
                if (callback != null) {
                    if (payment != null) {
                        callback.onSuccess(payment);
                    } else {
                        callback.onError("Payment not found");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to get payment: " + e.getMessage());
                }
            }
        });
    }

    public void getPaymentByOrderCode(long orderCode, PaymentCallback callback) {
        executorService.execute(() -> {
            try {
                PayosPayment payment = payosPaymentDao.getPaymentByOrderCode(orderCode);
                if (callback != null) {
                    if (payment != null) {
                        callback.onSuccess(payment);
                    } else {
                        callback.onError("Payment not found");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to get payment: " + e.getMessage());
                }
            }
        });
    }

    public void getPaymentsByUserId(int userId, PaymentsListCallback callback) {
        executorService.execute(() -> {
            try {
                List<PayosPayment> payments = payosPaymentDao.getPaymentsByUserId(userId);
                if (callback != null) {
                    callback.onSuccess(payments);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to get payments: " + e.getMessage());
                }
            }
        });
    }

    public void getPaymentsByStatus(String status, PaymentsListCallback callback) {
        executorService.execute(() -> {
            try {
                List<PayosPayment> payments = payosPaymentDao.getPaymentsByStatus(status);
                if (callback != null) {
                    callback.onSuccess(payments);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to get payments: " + e.getMessage());
                }
            }
        });
    }

    public List<PayosTransaction> parseTransactions(String transactionsJson) {
        if (transactionsJson == null || transactionsJson.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        Type listType = new TypeToken<List<PayosTransaction>>() {}.getType();
        return gson.fromJson(transactionsJson, listType);
    }

    public List<PayosTransaction> getTransactions(PayosPayment payment) {
        return parseTransactions(payment.getTransactionsJson());
    }

    public interface PaymentCallback {
        void onSuccess(PayosPayment payment);
        void onError(String error);
    }

    public interface PaymentsListCallback {
        void onSuccess(List<PayosPayment> payments);
        void onError(String error);
    }
}