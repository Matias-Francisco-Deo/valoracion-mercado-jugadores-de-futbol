package com.overcode.service.impl;

import com.overcode.model.AuditLog;
import com.overcode.model.FillPlan;
import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.events.P2POrderCreatedEvent;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.interfaces.P2POrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class P2POrderServiceImpl implements P2POrderService {

    private final P2POrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public P2POrderServiceImpl(P2POrderRepository orderRepository,
                               PortfolioRepository portfolioRepository,
                               AuditLogRepository auditLogRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public P2POrder createOrder(Long userId, Long playerId, OrderType type, int quantity, BigDecimal pricePerToken) {
        if (quantity <= 0 || pricePerToken.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity and price must be positive.");
        }

        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user: " + userId));

        P2POrder newOrder = new P2POrder(UUID.randomUUID(), userId, playerId, type, quantity,
                pricePerToken, OrderStatus.OPEN, Instant.now(), null);

        // 1. Escrow (Bloqueo de fondos/tokens)
        if (type == OrderType.BUY) {
            if (!portfolio.hasSufficientCredits(newOrder.totalValue())) {
                throw new InsufficientEscrowException("Not enough credits to place BUY order.");
            }
            portfolio.deductCredits(newOrder.totalValue());
        } else {
            TokenHolding holding = portfolio.getHoldingForPlayer(playerId)
                    .orElseThrow(() -> new InsufficientEscrowException("No tokens found for player."));
            if (!holding.hasSufficientTokens(quantity)) {
                throw new InsufficientEscrowException("Not enough tokens to place SELL order.");
            }
            holding.deductTokens(quantity);
        }

        portfolioRepository.save(portfolio);

        // 2. Guardar orden
        P2POrder savedOrder = orderRepository.save(newOrder);

        // 3. Emitir evento asincrónico
        eventPublisher.publishEvent(new P2POrderCreatedEvent(this, savedOrder.getId()));

        return savedOrder;
    }

    @Override
    @Transactional
    public void executeMarketBuy(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Portfolio buyerPortfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user: " + userId));

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
        for (FillPlan.Fill fill : plan.getFills()) {
            P2POrder makerOrder = fill.getMakerOrder();
            Portfolio sellerPortfolio = sellerPortfolios.computeIfAbsent(makerOrder.getUserId(),
                    id -> portfolioRepository.findByUserId(id)
                            .orElseThrow(() -> new IllegalStateException("Seller portfolio not found: " + id)));

            // Seller already escrowed the tokens when posting the SELL order; just move them to
            // the buyer and credit the seller. Then consume the resting quantity.
            sellerPortfolio.addCredits(fill.getSubtotal());
            buyerHolding.addTokens(fill.getQuantityTaken());
            makerOrder.reduceQuantity(fill.getQuantityTaken());
            orderRepository.save(makerOrder);

            auditLogRepository.save(new AuditLog(UUID.randomUUID(), AuditLog.OperationType.BUY,
                    userId, makerOrder.getUserId(), playerId,
                    fill.getQuantityTaken(), fill.getPrice(), Instant.now()));
        }

        sellerPortfolios.values().forEach(portfolioRepository::save);
        portfolioRepository.save(buyerPortfolio);
    }

    @Override
    @Transactional
    public void executeMarketSell(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Portfolio sellerPortfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user: " + userId));

        TokenHolding sellerHolding = sellerPortfolio.getHoldingForPlayer(playerId)
                .orElseThrow(() -> new InsufficientEscrowException("No tokens found for player."));
        if (!sellerHolding.hasSufficientTokens(quantity)) {
            throw new InsufficientEscrowException("Not enough tokens to sell.");
        }

        // Sweep the open BUY book (highest bid first), ignoring the taker's own resting orders.
        List<P2POrder> bids = orderRepository.findOpenBuyOrders(playerId).stream()
                .filter(order -> !order.belongsTo(userId))
                .toList();

        FillPlan plan = FillPlan.build(quantity, bids);
        if (!plan.isFullyFilled()) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to sell " + quantity + " tokens for player " + playerId);
        }

        // Taker delivers tokens up front and receives the escrowed credits the makers already locked.
        sellerHolding.deductTokens(quantity);
        sellerPortfolio.addCredits(plan.totalCost());

        Map<Long, Portfolio> buyerPortfolios = new HashMap<>();
        for (FillPlan.Fill fill : plan.getFills()) {
            P2POrder makerOrder = fill.getMakerOrder();
            Portfolio buyerPortfolio = buyerPortfolios.computeIfAbsent(makerOrder.getUserId(),
                    id -> portfolioRepository.findByUserId(id)
                            .orElseThrow(() -> new IllegalStateException("Buyer portfolio not found: " + id)));

            // Buyer already escrowed the credits when posting the BUY order; just deliver tokens
            // and consume the resting quantity.
            buyerPortfolio.getOrCreateHoldingForPlayer(playerId).addTokens(fill.getQuantityTaken());
            makerOrder.reduceQuantity(fill.getQuantityTaken());
            orderRepository.save(makerOrder);

            auditLogRepository.save(new AuditLog(UUID.randomUUID(), AuditLog.OperationType.SELL,
                    userId, makerOrder.getUserId(), playerId,
                    fill.getQuantityTaken(), fill.getPrice(), Instant.now()));
        }

        buyerPortfolios.values().forEach(portfolioRepository::save);
        portfolioRepository.save(sellerPortfolio);
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

        // Cambiar estado a CANCELLED
        order.cancel();
        orderRepository.save(order);

        // Revertir Escrow
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user: " + userId));

        if (order.getType() == OrderType.BUY) {
            portfolio.addCredits(order.totalValue());
        } else {
            TokenHolding holding = portfolio.getOrCreateHoldingForPlayer(order.getPlayerId());
            holding.addTokens(order.getQuantity());
        }

        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void processMatchmaking(UUID orderId) {
        P2POrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.OPEN) {
            return; // Ya se procesó o canceló
        }

        Optional<P2POrder> matchOpt = (order.getType() == OrderType.BUY)
                ? orderRepository.findBestSellMatch(order.getPlayerId(), order.getQuantity(), order.getPricePerToken())
                : orderRepository.findBestBuyMatch(order.getPlayerId(), order.getQuantity(), order.getPricePerToken());

        if (matchOpt.isPresent()) {
            P2POrder match = matchOpt.get();

            // Evitar auto-trading
            if (order.belongsTo(match.getUserId())) {
                return;
            }

            // Realizar liquidación
            order.markCompleted();
            match.markCompleted();

            orderRepository.save(order);
            orderRepository.save(match);

            // Identificar comprador y vendedor
            P2POrder buyOrder = order.getType() == OrderType.BUY ? order : match;
            P2POrder sellOrder = order.getType() == OrderType.SELL ? order : match;

            // La transacción ocurre al precio de la orden CREADA ANTES (maker) o según la lógica de negocio.
            // Generalmente, el precio que se ejecuta es el del maker (la orden que ya estaba en el libro).
            // Usaremos el precio del maker.
            BigDecimal execPrice = order.getCreatedAt().isBefore(match.getCreatedAt()) ? order.getPricePerToken() : match.getPricePerToken();
            BigDecimal totalAmount = execPrice.multiply(BigDecimal.valueOf(buyOrder.getQuantity()));

            // Portfolios
            Portfolio buyerPortfolio = portfolioRepository.findByUserId(buyOrder.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Buyer portfolio not found"));
            Portfolio sellerPortfolio = portfolioRepository.findByUserId(sellOrder.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Seller portfolio not found"));

            // Si el comprador es el taker y ofreció un precio mayor al del maker (seller),
            // se le debe devolver la diferencia, ya que su escrow fue por el precio original (más alto).
            if (buyOrder.getPricePerToken().compareTo(execPrice) > 0) {
                BigDecimal refund = buyOrder.getPricePerToken().subtract(execPrice)
                        .multiply(BigDecimal.valueOf(buyOrder.getQuantity()));
                buyerPortfolio.addCredits(refund);
            }

            // Entregar tokens al comprador
            TokenHolding buyerHolding = buyerPortfolio.getOrCreateHoldingForPlayer(buyOrder.getPlayerId());
            buyerHolding.addTokens(buyOrder.getQuantity());

            // Entregar créditos al vendedor
            sellerPortfolio.addCredits(totalAmount);

            portfolioRepository.save(buyerPortfolio);
            portfolioRepository.save(sellerPortfolio);

            // Registrar AuditLog
            AuditLog log = new AuditLog(UUID.randomUUID(), AuditLog.OperationType.BUY,
                    buyOrder.getUserId(), sellOrder.getUserId(), buyOrder.getPlayerId(),
                    buyOrder.getQuantity(), execPrice, Instant.now());
            auditLogRepository.save(log);
        }
    }
}
