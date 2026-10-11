package com.overcode.service.interfaces;

import com.overcode.model.P2POrder;
import java.math.BigDecimal;
import java.util.UUID;
import com.overcode.model.OrderType;

public interface P2POrderService {
    P2POrder createOrder(Long userId, Long playerId, OrderType type, int quantity, BigDecimal pricePerToken);
    void cancelOrder(UUID orderId, Long userId);
    void processMatchmaking(UUID orderId);

    /**
     * Market BUY: immediately sweep the open SELL book for the player, cheapest first, until
     * {@code quantity} tokens are filled. All-or-nothing — if the book cannot fully fill the
     * request it throws and nothing changes.
     */
    void executeMarketBuy(Long userId, Long playerId, int quantity);

    /**
     * Market SELL: immediately sweep the open BUY book for the player, highest bid first, until
     * {@code quantity} tokens are sold. All-or-nothing.
     */
    void executeMarketSell(Long userId, Long playerId, int quantity);
}
