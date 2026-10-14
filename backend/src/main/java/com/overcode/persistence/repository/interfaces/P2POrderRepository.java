package com.overcode.persistence.repository.interfaces;

import com.overcode.model.P2POrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface P2POrderRepository {
    P2POrder save(P2POrder order);

    /** Persists several orders in one batch instead of one round-trip per order. */
    List<P2POrder> saveAll(List<P2POrder> orders);

    Optional<P2POrder> findById(UUID id);

    /** Open SELL orders for a player, cheapest first then FIFO — the asks a market BUY sweeps. */
    List<P2POrder> findOpenSellOrders(Long playerId);
}
