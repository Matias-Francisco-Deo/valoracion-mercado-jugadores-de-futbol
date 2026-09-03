package com.overcode.service.interfaces;

import com.overcode.controller.dto.TradeResponse;

public interface TradeService {

    TradeResponse buy(Long buyerId, Long sellerId, Long playerId, Integer quantity);

    TradeResponse sell(Long sellerId, Long buyerId, Long playerId, Integer quantity);
}
