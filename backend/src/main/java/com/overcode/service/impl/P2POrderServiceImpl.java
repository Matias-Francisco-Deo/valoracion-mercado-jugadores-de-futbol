package com.overcode.service.impl;

import com.overcode.model.FillPlan;
import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.exception.PortfolioNotFoundException;
import com.overcode.service.interfaces.P2POrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class P2POrderServiceImpl implements P2POrderService {

    private static final String PORTFOLIO_NOT_FOUND_MESSAGE = "Portfolio not found for user: ";

    private final P2POrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;
    private final PlayerRepository playerRepository;

    public P2POrderServiceImpl(P2POrderRepository orderRepository,
                               PortfolioRepository portfolioRepository,
                               PlayerRepository playerRepository) {
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public P2POrder createSellOrder(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        //orden con precio del token al momento de crearla
        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + playerId));
        BigDecimal pricePerToken = BigDecimal.valueOf(player.getCurrentPrice());

        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND_MESSAGE + userId));

        TokenHolding holding = portfolio.getHoldingForPlayer(playerId)
                .orElseThrow(() -> new InsufficientEscrowException("No tokens found for player."));
        if (!holding.hasSufficientTokens(quantity)) {
            throw new InsufficientEscrowException("Not enough tokens to place SELL order.");
        }
        holding.deductTokens(quantity);
        portfolioRepository.save(portfolio);

        P2POrder newOrder = P2POrder.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .playerId(playerId)
                .type(OrderType.SELL)
                .quantity(quantity)
                .pricePerToken(pricePerToken)
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(null)
                .build();
        return orderRepository.save(newOrder);
    }

    @Override
    @Transactional
    public void executeMarketBuy(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Portfolio buyerPortfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND_MESSAGE + userId));

        // Sweep the open SELL book (cheapest first), ignoring the taker's own resting orders.
        List<P2POrder> asks = orderRepository.findOpenSellOrders(playerId).stream()
                .filter(order -> !order.belongsTo(userId))
                .toList();

        FillPlan plan = FillPlan.build(quantity, asks);
        if (!plan.isFullyFilled()) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to buy " + quantity + " tokens for player " + playerId);
        }

        // Taker pays up front at each maker's price; the domain guards the buyer's funds.
        buyerPortfolio.deductCredits(plan.totalCost());
        TokenHolding buyerHolding = buyerPortfolio.getOrCreateHoldingForPlayer(playerId);

        Map<Long, Portfolio> sellerPortfolios = new HashMap<>();
        List<P2POrder> touchedMakers = new ArrayList<>();
        for (FillPlan.Fill fill : plan.getFills()) {
            P2POrder makerOrder = fill.getMakerOrder();
            Portfolio sellerPortfolio = sellerPortfolios.computeIfAbsent(makerOrder.getUserId(),
                    id -> portfolioRepository.findByUserId(id)
                            .orElseThrow(() -> new PortfolioNotFoundException("Seller portfolio not found: " + id)));

            // Seller already escrowed the tokens when posting the SELL order; just move them to
            // the buyer and credit the seller. Then consume the resting quantity.
            sellerPortfolio.addCredits(fill.getSubtotal());
            buyerHolding.addTokens(fill.getQuantityTaken());
            makerOrder.reduceQuantity(fill.getQuantityTaken());
            touchedMakers.add(makerOrder);
        }

        orderRepository.saveAll(touchedMakers);
        sellerPortfolios.values().forEach(portfolioRepository::save);
        portfolioRepository.save(buyerPortfolio);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId, Long userId) {
        P2POrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only cancel your own orders.");
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Only OPEN orders can be cancelled.");
        }

        order.cancel();
        orderRepository.save(order);

        // Refund the tokens that were escrowed when the SELL order was created.
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND_MESSAGE + userId));
        portfolio.getOrCreateHoldingForPlayer(order.getPlayerId()).addTokens(order.getQuantity());
        portfolioRepository.save(portfolio);
    }
}
