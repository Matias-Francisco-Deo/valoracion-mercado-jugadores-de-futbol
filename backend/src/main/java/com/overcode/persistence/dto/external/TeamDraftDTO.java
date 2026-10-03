package com.overcode.persistence.dto.external;

import com.overcode.model.Player;
import com.overcode.model.Team;

import java.util.List;

public record TeamDraftDTO(String name, String league, List<PlayerDraftDTO> players) {
    public Team aModelo(List<Player> players) {
        return new Team(name, league, players);
    }
}
