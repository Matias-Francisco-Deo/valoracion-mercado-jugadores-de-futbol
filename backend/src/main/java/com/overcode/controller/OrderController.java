package com.overcode.controller;

import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.controller.dto.p2p.P2POrderResponseDTO;
import com.overcode.model.P2POrder;
import com.overcode.model.User;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for buying and selling player tokens")
public class OrderController {

    private final P2POrderService p2pOrderService;
    private final UserService userService;

    public OrderController(P2POrderService p2pOrderService, UserService userService) {
        this.p2pOrderService = p2pOrderService;
        this.userService = userService;
    }

    private Long getUserId(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Unauthorized");
        }
        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return user.getId();
    }

    /** Buys instantly against the resting SELL book, at the player's current price. All-or-nothing. */
    @PostMapping("/buy")
    public ResponseEntity<?> buyTokens(@Valid @RequestBody BuyTokenRequest request, Principal principal) {
        try {
            Long userId = getUserId(principal);
            p2pOrderService.executeMarketBuy(userId, request.getPlayerId(), request.getQuantity());

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
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
            throw e;
        }
        // InsufficientFundsException and PortfolioNotFoundException are mapped globally by
        // GlobalExceptionHandler (402 / 404 respectively).
    }

    /** Publishes a resting SELL order for {@code quantity} tokens, priced at the player's current price. */
    @PostMapping("/sell")
    public ResponseEntity<?> sellTokens(@Valid @RequestBody SellTokenRequest request, Principal principal) {
        try {
            Long userId = getUserId(principal);
            P2POrder order = p2pOrderService.createSellOrder(userId, request.getPlayerId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(P2POrderResponseDTO.from(order));
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
            throw e;
        }
    }

    /** Cancels a resting SELL order and refunds its escrowed tokens. */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            Principal principal,
            @PathVariable("id") UUID orderId) {

        try {
            Long userId = getUserId(principal);
            p2pOrderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
            throw e;
        }
    }
}
