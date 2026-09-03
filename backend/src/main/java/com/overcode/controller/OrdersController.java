package com.overcode.controller;

import com.overcode.controller.dto.BuyOrderRequest;
import com.overcode.controller.dto.SellOrderRequest;
import com.overcode.controller.dto.TradeResponse;
import com.overcode.service.interfaces.TradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersController { // TODO cambiar nombre?

    private final TradeService tradeService;

    public OrdersController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/orders/buy")
    public TradeResponse buy(@Valid @RequestBody BuyOrderRequest request) {
        return tradeService.buy(request.buyerId(), request.sellerId(), request.playerId(), request.quantity());
    }

    @PostMapping("/orders/sell")
    public TradeResponse sell(@Valid @RequestBody SellOrderRequest request) {
        return tradeService.sell(request.sellerId(), request.buyerId(), request.playerId(), request.quantity());
    }
}
