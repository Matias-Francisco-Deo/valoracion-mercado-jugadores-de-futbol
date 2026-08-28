package com.overcode.market.model;

import java.math.BigDecimal;

public class Wallet {
    private final String id;
    private final String ownerId;
    private BigDecimal balance;
    private final String currency = "USD";

    public Wallet(String id, String ownerId, BigDecimal balance) {
        this.id = id;
        this.ownerId = ownerId;
        this.balance = balance;
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public BigDecimal getBalance() { return balance; }
    public void credit(BigDecimal amount) { this.balance = this.balance.add(amount); }
    public void debit(BigDecimal amount) { this.balance = this.balance.subtract(amount); }
    public String getCurrency() { return currency; }
}
