package com.overcode.service.interfaces;

import com.overcode.model.P2POrder;
import java.math.BigDecimal;
import java.util.UUID;
import com.overcode.model.OrderType;

public interface P2POrderService {
    P2POrder createOrder(Long userId, Long playerId, OrderType type, int quantity, BigDecimal pricePerToken);
    void cancelOrder(UUID orderId, Long userId);
    void processMatchmaking(UUID orderId);
}
