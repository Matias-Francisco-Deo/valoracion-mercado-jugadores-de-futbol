package com.overcode.service.interfaces;

import com.overcode.model.BuyOrder;
import com.overcode.model.SellOrder;
import com.overcode.model.SellPost;
import com.overcode.model.dto.OrderSellSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface P2POrderService {

    /**
     * Publishes 1 resting SELL order per token for the requested quantity,
     * priced at the player's current price.
     */
    List<SellOrder> createSellOrders(SellPost command);

    /**
     * Instantly executes a market BUY against the cheapest resting SELL orders for the player.
     * All-or-nothing: if there is not enough liquidity, throws InsufficientLiquidityException.
     */
    void executeMarketBuy(BuyOrder buyOrder);

    /**
     * Cancels a resting SELL order owned by userId and returns the token to the seller.
     */
    void cancelOrder(Long orderId, Long userId);

    Page<OrderSellSummaryDTO> buscarOrdersSellSummaryAbiertas(String nombre, Pageable pageable);

    Page<OrderSellSummaryDTO> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId,String nombre,Pageable pageable);
}
