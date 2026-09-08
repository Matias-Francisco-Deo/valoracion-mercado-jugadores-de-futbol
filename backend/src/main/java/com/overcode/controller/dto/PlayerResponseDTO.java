package com.overcode.controller.dto;

import com.overcode.model.Player;

public record PlayerResponseDTO(Long id, String name, Integer currentPrice, Integer totalIssued) {

    public static PlayerResponseDTO desdeModelo(Player player) {
        if (player == null) return null;
        return new PlayerResponseDTO(
                player.getId(),
                player.getName(),
                player.getCurrentPrice(),
                player.getTotalTokensIssued()
        );
    }
}
