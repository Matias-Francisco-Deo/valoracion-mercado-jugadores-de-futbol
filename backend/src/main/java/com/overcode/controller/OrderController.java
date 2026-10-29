package com.overcode.controller;

import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.controller.dto.venta.OrderSellSummaryPageResponseDTO;
import com.overcode.model.venta.BuyOrder;
import com.overcode.model.venta.SellPost;
import com.overcode.model.venta.orden.ResumenOrdenesDeVenta;
import com.overcode.service.interfaces.TokenMarketService;
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

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for buying and selling player tokens")
public class OrderController {

    private final TokenMarketService tokenMarketService;

    public OrderController(TokenMarketService tokenMarketService, UserService userService) {
        this.tokenMarketService = tokenMarketService;
    }

    /** Buys instantly against the resting SELL book, at the player's current price. All-or-nothing. */
    @PostMapping("/buy")
    public ResponseEntity<Void> buy(@Valid @RequestBody BuyTokenRequest request, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        BuyOrder buyOrder = request.aModelo(userId);
        tokenMarketService.buy(buyOrder);

        return ResponseEntity.ok().build();
    }

    /** Publishes resting SELL orders (one per token) for the requested quantity. */
    @PostMapping("/sell")
    public ResponseEntity<Void> sell(@Valid @RequestBody SellTokenRequest request, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        SellPost command = request.aModelo(userId);
        tokenMarketService.sell(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /** Cancels a resting SELL order and returns the token to the seller. No se usa.*/
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            Principal principal,
            @PathVariable("id") Long orderId) {
        Long userId = Long.parseLong(principal.getName());
        tokenMarketService.cancel(orderId, userId);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/forSell")
    public ResponseEntity<OrderSellSummaryPageResponseDTO> searchOpenOrdersSell(
            @RequestParam(required = false) String nombre,
            Pageable pageable,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        Page<ResumenOrdenesDeVenta> ordersSell = tokenMarketService.buscarOrdersSellSummaryAbiertas(userId,nombre, pageable);

        OrderSellSummaryPageResponseDTO response = OrderSellSummaryPageResponseDTO.desdeModelo(ordersSell);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/ordersSell")
    public ResponseEntity<OrderSellSummaryPageResponseDTO> searchUserOpenOrdersSell(
            @PathVariable Long id,
            @RequestParam(required = false) String nombre,
            Pageable pageable
    ) {
        Page<ResumenOrdenesDeVenta> ordersSell = tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(id,nombre, pageable);

        OrderSellSummaryPageResponseDTO response = OrderSellSummaryPageResponseDTO.desdeModelo(ordersSell);

        return ResponseEntity.ok(response);
    }
}
