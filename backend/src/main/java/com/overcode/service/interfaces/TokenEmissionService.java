package com.overcode.service.interfaces;

import com.overcode.model.player.Player;

import java.util.List;

public interface TokenEmissionService {
    
    /**
     * Emits 100 initial tokens for a newly discovered player, 
     * assigning them to the Super Admin's portfolio.
     */
    void emitTokensForNewPlayer(Player player);

    /**
     * Emits 100 initial tokens in batch for newly discovered players,
     * assigning them to the Super Admin's portfolio in a single operation.
     */
    void emitTokensForNewPlayers(List<Player> players);
}

