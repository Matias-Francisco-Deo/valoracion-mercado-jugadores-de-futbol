package com.overcode.controller.dto.player;

import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;

public record PlayerGameDataResponseDTO(
                                        Long id,
                                        Long playerId,
                                        Integer goals,
                                        Integer shotsOnTarget,
                                        Integer tackles,
                                        Double rating,
                                        PlayerPosition position
) {

    public static PlayerGameDataResponseDTO desdeModelo(PlayerGameData playerGameData, Long playerId) {
        if (playerGameData == null) return null;
        return new PlayerGameDataResponseDTO(
                playerGameData.getId(),
                playerId,
                playerGameData.getGoals(),
                playerGameData.getShotsOnTarget(),
                playerGameData.getTackles(),
                playerGameData.getRating(),
                playerGameData.getPosition()
        );
    }
}
