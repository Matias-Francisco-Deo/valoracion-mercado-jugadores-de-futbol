package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class PlayerFilter {

    private final String filterContent;

    public PlayerFilter(String filterContent) {
        this.filterContent = filterContent;
    }

    public abstract List<Player> getFilteredPlayers(PlayerRepository playerRepository);
}
