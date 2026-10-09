package com.overcode.persistence.repository.dao.jpa;

public record TokenForSaleProjection(
        Long idPlayer,
        String playerName,
        Integer quantity,
        Long pricePerToken
) {
}
