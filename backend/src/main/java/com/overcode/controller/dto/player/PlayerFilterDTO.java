package com.overcode.controller.dto.player;

public record PlayerFilterDTO(
        String clubName, // TODO validaciones?
        String league
) {
    public PlayerFilter aModelo() {
        return new PlayerFilter(clubName, league);
    }
}
