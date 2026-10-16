package com.overcode.controller.dto.token;

import com.overcode.model.dto.SummaryTokenPlayerDTO;

public record SummaryTokenPlayerResponseDTO(
        Long idPlayer,
        String playerName,
        Integer quantity,
        Double pricePerToken
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