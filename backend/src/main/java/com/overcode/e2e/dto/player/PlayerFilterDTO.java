package com.overcode.e2e.dto.player;

public record PlayerFilterDTO(
        String clubName,
        String league
) {
    public PlayerFilter aModelo() {
        return new PlayerFilter(clubName, league);
    }
}
