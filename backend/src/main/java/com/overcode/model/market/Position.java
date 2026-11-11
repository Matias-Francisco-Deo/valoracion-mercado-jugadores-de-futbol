package com.overcode.model.market;

import com.overcode.model.position.PlayerPositionData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    private Long playerId;
    private String playerName;

    private Integer quantity;
    private Double averagePurchasePrice;
    private Double currentPrice;
    private Double currentValue;
    private Double profitLoss;
    private Double profitLossPercentage;

    public Position(Long playerId, String playerName, Integer quantity,Double averagePurchasePrice, Double currentPrice, Double currentValue, Double profitLoss, Double profitLossPercentage) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.quantity = quantity;
        this.averagePurchasePrice = averagePurchasePrice;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercentage = profitLossPercentage;
    }

    public static Position desdePositionData(PlayerPositionData data) {

        Integer quantity = Math.toIntExact(data.totalBought() - data.totalSold());

        Double averagePurchasePrice = data.totalBought() > 0 ? data.totalPurchaseAmount() / data.totalBought() : 0.0;

        double currentPrice = data.currentPrice();
        double currentValue = quantity * currentPrice;
        double profitLoss = currentValue - (quantity * averagePurchasePrice);

        Double profitLossPercentage = quantity * averagePurchasePrice > 0 ? (profitLoss / (quantity * averagePurchasePrice)) * 100 : 0.0;

        return new Position(data.playerId(), data.playerName(), quantity, averagePurchasePrice, currentPrice, currentValue, profitLoss, profitLossPercentage);
    }

}

