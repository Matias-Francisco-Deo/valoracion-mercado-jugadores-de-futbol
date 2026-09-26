package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;

import java.util.List;

public class PlayerFilterByLeague extends PlayerFilter{

    public PlayerFilterByLeague(String filterContent) {
        super(filterContent);
    }

    @Override
    public List<Player> getFilteredPlayers(PlayerRepository playerRepository) {
        return playerRepository.listarJugadoresPorLiga(this.getFilterContent());
    }
}
