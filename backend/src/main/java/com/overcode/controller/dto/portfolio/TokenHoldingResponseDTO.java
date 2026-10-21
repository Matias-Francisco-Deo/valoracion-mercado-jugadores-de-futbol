package com.overcode.controller.dto.portfolio;

import com.overcode.model.token.TokenHolding;

public class TokenHoldingResponseDTO {
    private final Long playerId;
    private final int quantity;

    public TokenHoldingResponseDTO(Long playerId, int quantity) {
        this.playerId = playerId;
        this.quantity = quantity;
    }

    public static TokenHoldingResponseDTO from(TokenHolding holding) {
        return new TokenHoldingResponseDTO(holding.getPlayerId(), holding.getQuantity());
    }

    public Long getPlayerId() { return playerId; }
    public int getQuantity() { return quantity; }
}
