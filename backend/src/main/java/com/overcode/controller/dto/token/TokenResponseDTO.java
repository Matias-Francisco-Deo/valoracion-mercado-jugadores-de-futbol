package com.overcode.controller.dto.token;

import com.overcode.model.Token;

public record TokenResponseDTO(Long tokenId, Long ownerId, Long playerId) {

    public static TokenResponseDTO desdeModelo(Token token) {
        return new TokenResponseDTO(token.getId(),
                token.getOwner() != null ? token.getOwner().getId() : null,
                token.getPlayer() != null ? token.getPlayer().getId() : null);
    }
}
