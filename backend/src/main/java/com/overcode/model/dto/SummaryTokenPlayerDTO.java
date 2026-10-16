package com.overcode.model.dto;

public record SummaryTokenPlayerDTO(
        Long idPlayer,
        String playerName,
        Long quantity,
        Integer pricePerToken
) {
}
