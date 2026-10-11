package com.overcode.persistence.repository.interfaces;

import com.overcode.model.P2POrder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface P2POrderRepository {
    P2POrder save(P2POrder order);
    Optional<P2POrder> findById(UUID id);
    Optional<P2POrder> findBestSellMatch(Long playerId, int quantity, BigDecimal maxPrice);
    Optional<P2POrder> findBestBuyMatch(Long playerId, int quantity, BigDecimal minPrice);

    /** Open SELL orders for a player, cheapest first then FIFO — the asks a market BUY sweeps. */
    List<P2POrder> findOpenSellOrders(Long playerId);

    /** Open BUY orders for a player, highest bid first then FIFO — the bids a market SELL sweeps. */
    List<P2POrder> findOpenBuyOrders(Long playerId);
}
