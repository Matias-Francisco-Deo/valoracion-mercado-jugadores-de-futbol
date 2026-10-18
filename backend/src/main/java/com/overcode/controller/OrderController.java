package com.overcode.controller;

import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.controller.dto.p2p.OrderSellSummaryPageResponseDTO;
import com.overcode.controller.dto.p2p.OrderSellSummaryResponseDTO;
import com.overcode.controller.dto.p2p.SellOrderResponseDTO;
import com.overcode.model.BuyOrder;
import com.overcode.model.SellOrder;
import com.overcode.model.SellPost;
import com.overcode.model.User;
import com.overcode.model.dto.OrderSellSummaryDTO;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for buying and selling player tokens")
public class OrderController {

    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    private static final String ERROR_KEY = "error";

    private final P2POrderService p2pOrderService;
    private final UserService userService;

    public OrderController(P2POrderService p2pOrderService, UserService userService) {
        this.p2pOrderService = p2pOrderService;
        this.userService = userService;
    }

    private Long getUserId(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException(UNAUTHORIZED_MESSAGE);
        }
        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return user.getId();
    }

    /** Buys instantly against the resting SELL book, at the player's current price. All-or-nothing. */
    @PostMapping("/buy")
    public ResponseEntity<Object> buyTokens(@Valid @RequestBody BuyTokenRequest request, Principal principal) {
        try {
            Long userId = getUserId(principal);
            BuyOrder buyOrder = request.aModelo(userId);
            p2pOrderService.executeMarketBuy(buyOrder);

            return ResponseEntity.ok(Map.of(
                    "message", "Tokens successfully purchased",
                    "playerId", request.getPlayerId(),
                    "quantity", request.getQuantity(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (InsufficientLiquidityException e) {
            return ResponseEntity.status(409).body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (IllegalStateException e) {
            if (e.getMessage().equals(UNAUTHORIZED_MESSAGE)) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, e.getMessage()));
            }
            throw e;
        }
    }

    /** Publishes resting SELL orders (one per token) for the requested quantity. */
    @PostMapping("/sell")
    public ResponseEntity<Object> sellTokens(@Valid @RequestBody SellTokenRequest request, Principal principal) {
        try {
            Long userId = getUserId(principal);
            SellPost command = request.aModelo(userId);
            List<SellOrder> orders = p2pOrderService.createSellOrders(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(SellOrderResponseDTO.from(orders));
        } catch (IllegalStateException e) {
            if (e.getMessage().equals(UNAUTHORIZED_MESSAGE)) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, e.getMessage()));
            }
            throw e;
        }
    }

    /** Cancels a resting SELL order and returns the token to the seller. */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelOrder(
            Principal principal,
            @PathVariable("id") Long orderId) {

        try {
            Long userId = getUserId(principal);
            p2pOrderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            if (e.getMessage().equals(UNAUTHORIZED_MESSAGE)) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, e.getMessage()));
            }
            throw e;
        }
    }

    @GetMapping("/for-sell")
    public ResponseEntity<OrderSellSummaryPageResponseDTO> searchOpenOrdersSell(
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 20, sort = "playerName")
            Pageable pageable,
            Principal principal
    ) {
        Long userId = principal != null ? getUserId(principal) : null;

        Page<OrderSellSummaryDTO> ordersSell = p2pOrderService.buscarOrdersSellSummaryAbiertas(userId,nombre, pageable);

        OrderSellSummaryPageResponseDTO response = OrderSellSummaryPageResponseDTO.desdeModelo(ordersSell);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/ordersSell")
    public ResponseEntity<OrderSellSummaryPageResponseDTO> searchUserOpenOrdersSell(
            @PathVariable Long id,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, sort = "playerName")
            Pageable pageable
    ) {
        Page<OrderSellSummaryDTO> ordersSell = p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(id,nombre, pageable);

        OrderSellSummaryPageResponseDTO response = OrderSellSummaryPageResponseDTO.desdeModelo(ordersSell);

        return ResponseEntity.ok(response);
    }
}
