package com.overcode.model;

import java.time.Instant;

public class Transaction {

    private Long id;
    private Instant timestamp;
    private String type;
    private Long buyerId;
    private Long sellerId;
    private Long playerId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalAmount;

    public Transaction(Long id, Instant timestamp, String type, Long buyerId, Long sellerId,
                      Long playerId, Integer quantity, Integer unitPrice, Integer totalAmount) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }
        if (type == null || (!type.equals("BUY") && !type.equals("SELL"))) {
            throw new IllegalArgumentException("Type must be BUY or SELL");
        }
        if (playerId == null) {
            throw new IllegalArgumentException("Player id is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (totalAmount == null || totalAmount <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.playerId = playerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }
}
