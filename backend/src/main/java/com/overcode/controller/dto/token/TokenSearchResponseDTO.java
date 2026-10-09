package com.overcode.controller.dto.token;

import com.overcode.model.Player;
import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;

public record TokenSearchResponseDTO(
        Long idPlayer,
        String playerName,
        Integer quantity,
        Long currentPrice
) {
    public static TokenSearchResponseDTO desdeModelo(TokenForSaleProjection token){
        if (token == null) return null;
        return  new TokenSearchResponseDTO(
                token.idPlayer(),
                token.playerName(),
                token.quantity(),
                token.pricePerToken()
        );
    }
}
