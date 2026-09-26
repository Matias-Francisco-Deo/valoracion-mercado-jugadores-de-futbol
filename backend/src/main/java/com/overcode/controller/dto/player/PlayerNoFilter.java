package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;

import java.util.List;

public class PlayerNoFilter extends PlayerFilter{
    public PlayerNoFilter() {
        super(null);
    }

    @Override
    public List<Player> getFilteredPlayers(PlayerDAOJPA playerDAOJPA) {
        return playerDAOJPA.findAllByOrderByIdAsc().stream().map(PlayerJPADTO::aModelo).toList();
    }
}
