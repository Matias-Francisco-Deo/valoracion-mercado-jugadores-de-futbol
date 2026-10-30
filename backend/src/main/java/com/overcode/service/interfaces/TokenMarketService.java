package com.overcode.service.interfaces;

import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TokenMarketService {

    /**
     * Publishes 1 resting SELL order per token for the requested quantity,
     * priced at the player's current price.
     */
    List<OrdenDeVenta> sell(PeticionVenta command);

    /**
     * Instantly executes a market BUY against the cheapest resting SELL orders for the player.
     * All-or-nothing: if there is not enough liquidity, throws InsufficientLiquidityException.
     */
    void buy(OrdenDeCompra buyOrder);

    /**
     * Cancels a resting SELL order owned by userId and returns the token to the seller.
     */
    void cancel(Long orderId, Long userId);

    Page<ResumenOrdenesDeVenta> buscarOrdersSellSummaryAbiertas(Long userId, String nombre, Pageable pageable);

    Page<ResumenOrdenesDeVenta> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId, String nombre, Pageable pageable);
}
