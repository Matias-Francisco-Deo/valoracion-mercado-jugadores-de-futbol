package com.overcode.controller;

import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.User;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/p2p/orders")
public class P2POrderController {

    private final P2POrderService p2pOrderService;
    private final UserService userService;

    public P2POrderController(P2POrderService p2pOrderService, UserService userService) {
        this.p2pOrderService = p2pOrderService;
        this.userService = userService;
    }

    public static class CreateOrderRequest {
        public Long playerId;
        public OrderType type;
        public int quantity;
        public BigDecimal pricePerToken;
    }

    private Long getUserId(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Unauthorized");
        }
        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            Principal principal,
            @RequestBody CreateOrderRequest request) {
        
        try {
            Long userId = getUserId(principal);
            P2POrder order = p2pOrderService.createOrder(
                    userId, request.playerId, request.type, request.quantity, request.pricePerToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
            throw e;
        }
    }

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
