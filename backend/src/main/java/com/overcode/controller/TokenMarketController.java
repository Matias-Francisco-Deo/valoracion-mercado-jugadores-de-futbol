package com.overcode.controller;

import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.model.User;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Tag(name = "Market Orders", description = "Endpoints for buying and selling player tokens")
public class TokenMarketController {

    private final P2POrderService p2pOrderService;
    private final UserService userService;

    public TokenMarketController(P2POrderService p2pOrderService, UserService userService) {
        this.p2pOrderService = p2pOrderService;
        this.userService = userService;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyTokens(@Valid @RequestBody BuyTokenRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            p2pOrderService.executeMarketBuy(user.getId(), request.getPlayerId(), request.getQuantity());

            return ResponseEntity.ok(Map.of(
                    "message", "Tokens successfully purchased",
                    "playerId", request.getPlayerId(),
                    "quantity", request.getQuantity(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (InsufficientLiquidityException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            if ("Insufficient funds".equals(e.getMessage())) {
                return ResponseEntity.status(402).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellTokens(@Valid @RequestBody SellTokenRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            p2pOrderService.executeMarketSell(user.getId(), request.getPlayerId(), request.getQuantity());

            return ResponseEntity.ok(Map.of(
                    "message", "Tokens successfully sold",
                    "playerId", request.getPlayerId(),
                    "quantity", request.getQuantity(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (InsufficientEscrowException e) {
            // Seller does not own enough tokens to cover the sale.
            return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
        } catch (InsufficientLiquidityException e) {
            // No open bids to absorb the sale.
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }
}
