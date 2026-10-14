package com.overcode.persistence.dto.jpa;

public record SummaryTokenPlayerDTO(
        Long idPlayer,
        String playerName,
        Long quantity,
        Integer pricePerToken
) {
}
