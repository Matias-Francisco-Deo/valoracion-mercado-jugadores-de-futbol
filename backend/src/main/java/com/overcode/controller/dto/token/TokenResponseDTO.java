package com.overcode.controller.dto.token;

import com.overcode.model.Token;

public record TokenResponseDTO(Long tokenId, Long ownerId, Long playerId) {

    public static TokenResponseDTO desdeModelo(Token token) {
        return new TokenResponseDTO(token.getId(), token.getOwner().getId(), token.getPlayer().getId());
    }
}
