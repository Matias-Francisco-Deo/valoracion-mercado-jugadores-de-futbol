package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;

import java.util.List;

public class PlayerNoFilter extends PlayerFilter{
    public PlayerNoFilter() {
        super(null);
    }

    @Override
    public List<Player> getFilteredPlayers(PlayerRepository playerRepository) {
        return playerRepository.recuperarTodos();
    }
}
