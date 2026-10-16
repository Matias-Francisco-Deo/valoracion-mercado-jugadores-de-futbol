package com.overcode.model.dto;

public record SummaryTokenPlayerDTO(
        Long idPlayer,
        String playerName,
        Integer quantity,
        Double pricePerToken
) {
}
