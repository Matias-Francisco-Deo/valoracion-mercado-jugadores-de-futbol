package com.overcode.service.interfaces;

public interface TokenMarketService {
    
    /**
     * Buys tokens from the market.
     * 
     * @param userId The ID of the user buying tokens.
     * @param playerId The ID of the player whose tokens are being bought.
     * @param quantity The number of tokens to buy.
     */
    void buyTokens(Long userId, Long playerId, int quantity);

    /**
     * Sells tokens back to the market.
     * 
     * @param userId The ID of the user selling tokens.
     * @param playerId The ID of the player whose tokens are being sold.
     * @param quantity The number of tokens to sell.
     */
    void sellTokens(Long userId, Long playerId, int quantity);
}

