package com.overcode.model.player;

import java.util.List;

public record TeamUpsertResult(Team team, List<Player> newPlayers) {
}
