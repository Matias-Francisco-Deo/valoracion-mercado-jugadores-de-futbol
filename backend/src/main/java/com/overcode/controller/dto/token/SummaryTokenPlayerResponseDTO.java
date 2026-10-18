package com.overcode.controller.dto.token;

import com.overcode.model.dto.SummaryTokenPlayerDTO;

public record SummaryTokenPlayerResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
    public static SummaryTokenPlayerResponseDTO desdeModelo(SummaryTokenPlayerDTO modelo) {
        return new SummaryTokenPlayerResponseDTO(
                modelo.idPlayer(),
                modelo.playerName(),
                modelo.quantity(),
                modelo.pricePerToken()
        );
    }
}