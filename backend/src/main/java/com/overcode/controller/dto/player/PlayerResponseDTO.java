package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.model.Token;

import java.util.List;
import java.util.stream.Collectors;

public record PlayerResponseDTO(Long id, String name, Integer currentPrice, List<Long> tokens) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                player.getTokens().stream().map(Token::getId).collect(Collectors.toList()) // TODO con id llega?-> NO, agregarle id de jugador y de usuario
        );
    }
}
