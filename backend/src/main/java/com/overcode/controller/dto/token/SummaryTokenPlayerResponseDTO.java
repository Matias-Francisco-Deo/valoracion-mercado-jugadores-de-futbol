package com.overcode.controller.dto.token;

import com.overcode.model.token.SummaryTokenPlayer;

public record SummaryTokenPlayerResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
    public static SummaryTokenPlayerResponseDTO desdeModelo(SummaryTokenPlayer modelo) {
        return new SummaryTokenPlayerResponseDTO(
                modelo.idPlayer(),
                modelo.playerName(),
                modelo.quantity(),
                modelo.pricePerToken()
        );
    }
}