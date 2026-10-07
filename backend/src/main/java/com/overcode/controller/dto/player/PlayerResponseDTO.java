package com.overcode.controller.dto.player;

import com.overcode.controller.dto.token.TokenResponseDTO;
import com.overcode.model.Player;

import java.util.List;

public record PlayerResponseDTO(Long id,
                                String name,
                                Integer currentPrice,
                                TeamResponseDTO team,
                                PlayerGameDataResponseDTO playerGameData,
                                List<TokenResponseDTO> tokens) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                TeamResponseDTO.desdeModelo(player.getTeam()),
                PlayerGameDataResponseDTO.desdeModelo(player.getPlayerGameData(), player.getId()),
                player.getTokens().stream().map(TokenResponseDTO::desdeModelo).toList()
        );
    }
}
