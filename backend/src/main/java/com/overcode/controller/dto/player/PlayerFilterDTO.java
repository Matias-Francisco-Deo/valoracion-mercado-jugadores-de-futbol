package com.overcode.controller.dto.player;

public record PlayerFilterDTO(
        String clubName,
        String league
) {
    public PlayerFilter aModelo() {
        return new PlayerFilter(clubName, league);
    }
}
