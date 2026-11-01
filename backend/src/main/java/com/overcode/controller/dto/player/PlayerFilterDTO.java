package com.overcode.controller.dto.player;

public record PlayerFilterDTO(
        String clubName,
        String league,
        String position
) {
    public PlayerFilter aModelo() {
        return new PlayerFilter(clubName, league, position);
    }
}
