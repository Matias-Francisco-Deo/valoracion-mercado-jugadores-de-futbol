package com.overcode.controller.dto.player;

import com.overcode.controller.dto.token.TokenResponseDTO;
import com.overcode.model.Player;
import com.overcode.model.Token;

import java.util.List;
import java.util.stream.Collectors;

public record PlayerResponseDTO(Long id, String name, Integer currentPrice, List<TokenResponseDTO> tokens) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                player.getTokens().stream().map(TokenResponseDTO::desdeModelo).collect(Collectors.toList())
        );
    }
}
