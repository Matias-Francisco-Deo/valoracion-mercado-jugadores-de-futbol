package com.overcode.service.interfaces;

import com.overcode.model.Player;

public interface TokenEmissionService {
    
    /**
     * Emits 100 initial tokens for a newly discovered player, 
     * assigning them to the Super Admin's portfolio.
     */
    void emitTokensForNewPlayer(Player player);
}
