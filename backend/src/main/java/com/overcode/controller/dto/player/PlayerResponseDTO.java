package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.model.Token;

import java.util.List;

public record PlayerResponseDTO(Long id, String name, Integer currentPrice, List<Token> tokens) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                player.getTokens() // TODO recursión?
        );
    }
}
