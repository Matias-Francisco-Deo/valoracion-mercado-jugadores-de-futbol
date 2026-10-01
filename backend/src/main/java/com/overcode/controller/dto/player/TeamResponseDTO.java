package com.overcode.controller.dto.player;

import com.overcode.model.Team;

public record TeamResponseDTO(Long id,
                              String name,
                              String league) {

    public static TeamResponseDTO desdeModelo(Team team) {
        if (team == null) return null;
        return new TeamResponseDTO(
                team.getId(),
                team.getName(),
                team.getLeague()
        );
    }
}
