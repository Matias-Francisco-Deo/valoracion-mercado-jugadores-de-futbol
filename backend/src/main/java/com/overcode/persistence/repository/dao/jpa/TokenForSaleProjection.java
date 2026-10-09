package com.overcode.persistence.repository.dao.jpa;

public record TokenForSaleProjection(
        Long idPlayer,
        String playerName,
        Long quantity,
        Integer pricePerToken
) {
}
