package com.overcode.market.model;

import java.math.BigDecimal;
import java.util.UUID;

public class MarketTrade {
    private final String id;
    private final String playerId;
    private final String buyerId;
    private final String sellerId;
    private final int quantity;
    private final BigDecimal unitQuotation;
    private final BigDecimal totalValue;
    private final String status;
    private final String reason;

    public MarketTrade(String playerId, String buyerId, String sellerId, int quantity, BigDecimal unitQuotation, String status, String reason) {
        this.id = UUID.randomUUID().toString();
        this.playerId = playerId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.unitQuotation = unitQuotation;
        this.totalValue = unitQuotation.multiply(java.math.BigDecimal.valueOf(quantity));
        this.status = status;
        this.reason = reason;
    }

    public String getId() { return id; }
    public String getPlayerId() { return playerId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitQuotation() { return unitQuotation; }
    public BigDecimal getTotalValue() { return totalValue; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
}
