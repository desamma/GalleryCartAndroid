package com.example.gallerycart.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PayosTransaction {

    private long amount;
    private String description;

    @SerializedName("accountNumber")
    private String accountNumber;

    private String reference;

    @SerializedName("transactionDateTime")
    private Date transactionDateTime;

    public PayosTransaction() {}

    public PayosTransaction(long amount, String description, String accountNumber,
                            String reference, Date transactionDateTime) {
        this.amount = amount;
        this.description = description;
        this.accountNumber = accountNumber;
        this.reference = reference;
        this.transactionDateTime = transactionDateTime;
    }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Date getTransactionDateTime() { return transactionDateTime; }
    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }
}