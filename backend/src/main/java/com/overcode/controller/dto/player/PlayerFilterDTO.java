package com.overcode.controller.dto.player;

import com.overcode.model.player.PlayerFilter;

public record PlayerFilterDTO(
        String clubName,
        String league,
        String position
) {
    public PlayerFilter aModelo() {
        return new PlayerFilter(clubName, league, position);
    }
}
