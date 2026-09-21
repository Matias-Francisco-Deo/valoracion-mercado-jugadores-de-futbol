package com.overcode.controller.dto.player;

import com.overcode.controller.dto.token.TokenResponseDTO;
import com.overcode.model.Player;

import java.util.List;
import java.util.stream.Collectors;

public record PlayerResponseDTO(Long id,
                                String name,
                                Integer currentPrice,
                                String clubName,
                                Integer goals,
                                Integer shotsOnTarget,
                                Integer passes,
                                Integer interceptions,
                                Integer tackles,
                                Double rating,
                                List<TokenResponseDTO> tokens) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                player.getClubName(),
                player.getGoals(),
                player.getShotsOnTarget(),
                player.getPasses(),
                player.getInterceptions(),
                player.getTackles(),
                player.getRating(),
                player.getTokens().stream().map(TokenResponseDTO::desdeModelo).collect(Collectors.toList())
        );
    }
}
