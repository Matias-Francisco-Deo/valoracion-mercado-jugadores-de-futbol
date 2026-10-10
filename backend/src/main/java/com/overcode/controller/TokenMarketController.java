package com.overcode.controller;

import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.model.User;
import com.overcode.service.interfaces.UserService;
import com.overcode.service.interfaces.TokenMarketService;
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

    private final TokenMarketService tokenMarketService;
    private final UserService userService;

    public TokenMarketController(TokenMarketService tokenMarketService, UserService userService) {
        this.tokenMarketService = tokenMarketService;
        this.userService = userService;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyTokens(@Valid @RequestBody BuyTokenRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = principal.getName();
        User user = userService.recuperarPorEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            tokenMarketService.buyTokens(user.getId(), request.getPlayerId(), request.getQuantity());

            return ResponseEntity.ok(Map.of(
                    "message", "Tokens successfully purchased",
                    "playerId", request.getPlayerId(),
                    "quantity", request.getQuantity(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Insufficient funds")) {
                return ResponseEntity.status(402).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().contains("tokens")) {
                return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellTokens(@Valid @RequestBody SellTokenRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = principal.getName();
        User user = userService.recuperarPorEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            tokenMarketService.sellTokens(user.getId(), request.getPlayerId(), request.getQuantity());

            return ResponseEntity.ok(Map.of(
                    "message", "Tokens successfully sold",
                    "playerId", request.getPlayerId(),
                    "quantity", request.getQuantity(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Not enough tokens to sell") || e.getMessage().contains("You don't own")) {
                return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().equals("Insufficient funds")) {
                // E.g., admin doesn't have enough credits to buy back
                return ResponseEntity.status(409).body(Map.of("error", "Market lacks credits to fulfill this sale"));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

