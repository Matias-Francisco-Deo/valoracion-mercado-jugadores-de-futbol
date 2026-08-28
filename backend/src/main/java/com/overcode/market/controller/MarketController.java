package com.overcode.market.controller;

import com.overcode.market.dto.BuyTokenRequest;
import com.overcode.market.dto.BuyTokenResponse;
import com.overcode.market.dto.MarketInitializationResponse;
import com.overcode.market.service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    private final MarketService service;

    public MarketController(MarketService service) {
        this.service = service;
    }

    @PostMapping("/init")
    public ResponseEntity<MarketInitializationResponse> init() {
        return ResponseEntity.ok(service.initMarket());
    }

    @GetMapping("/players")
    public ResponseEntity<?> players() {
        return ResponseEntity.ok(service.initMarket()); // temporary: return initialization status
    }

    @PostMapping("/users/{userId}/buy")
    public ResponseEntity<BuyTokenResponse> buy(@PathVariable String userId, @RequestBody BuyTokenRequest req) {
        BuyTokenResponse resp = service.buy(userId, req);
        return ResponseEntity.ok(resp);
    }
}
