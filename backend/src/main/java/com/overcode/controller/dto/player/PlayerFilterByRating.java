package com.overcode.controller.dto.player;

import com.overcode.model.Player;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;

import java.util.List;

public class PlayerFilterByRating extends PlayerFilter{

    public PlayerFilterByRating(String filterContent) {
        super(filterContent);
    }

    @Override
    public List<Player> getFilteredPlayers(PlayerDAOJPA playerDAOJPA) {
        return List.of();
    }
}
