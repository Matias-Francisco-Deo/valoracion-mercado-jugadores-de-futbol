package com.overcode.service.interfaces;

import com.overcode.model.P2POrder;
import java.util.UUID;

/**
 * Trading model: buying is always an instant, all-or-nothing taker sweep against the resting
 * SELL book; selling publishes a resting SELL order that waits until one or more buyers consume
 * it (fully or partially). There is no resting BUY order and no user-supplied price anywhere in
 * this API — a player's price is owned by the pricing strategy ({@code Player#getCurrentPrice()})
 * and is frozen onto a SELL order at creation time.
 */
public interface P2POrderService {

    /** Publishes a resting SELL order for {@code quantity} tokens, priced at the player's current price. */
    P2POrder createSellOrder(Long userId, Long playerId, int quantity);

    /** Cancels a resting SELL order owned by {@code userId} and refunds its escrowed tokens. */
    void cancelOrder(UUID orderId, Long userId);

    /**
     * Instantly sweeps the open SELL book for the player, cheapest first, until {@code quantity}
     * tokens are filled. All-or-nothing — if the book cannot fully fill the request it throws and
     * nothing changes.
     */
    void executeMarketBuy(Long userId, Long playerId, int quantity);
}
