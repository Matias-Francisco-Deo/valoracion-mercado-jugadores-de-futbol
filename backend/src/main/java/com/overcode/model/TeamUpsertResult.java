package com.overcode.model;

import java.util.List;

public record TeamUpsertResult(Team team, List<Player> newPlayers) {
    public TeamUpsertResult {
        if (newPlayers == null) {
            newPlayers = List.of();
        }
    }
}
