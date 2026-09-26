package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;

import java.util.List;

public class PlayerFilterByClub extends PlayerFilter{

    public PlayerFilterByClub(String filterContent) {
        super(filterContent);
    }

    @Override
    public List<Player> getFilteredPlayers(PlayerDAOJPA playerDAOJPA) {

        return playerDAOJPA.listarJugadoresPorClub(getFilterContent()).stream().map(PlayerJPADTO::aModelo).toList();
    }
}
