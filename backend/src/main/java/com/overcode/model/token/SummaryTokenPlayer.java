package com.overcode.model.token;

public record SummaryTokenPlayer(
        Long idPlayer,
        String playerName,
        Integer quantity,
        Double pricePerToken
) {
}
