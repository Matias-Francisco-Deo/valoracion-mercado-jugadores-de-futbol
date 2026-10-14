package com.overcode.service.impl;

import com.overcode.model.*;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.exception.*;
import com.overcode.service.interfaces.P2POrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

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
    public P2POrder createSellOrder(Long userId, Long playerId, int quantity) { // TODO esto tiene que ser un solo argumento SellOrder
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive."); // TODO errores de service
        }

        //orden con precio del token al momento de crearla
        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + playerId));
        BigDecimal pricePerToken = BigDecimal.valueOf(player.getCurrentPrice());

        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND_MESSAGE + userId)); // TODO esta no va

        TokenHolding holding = portfolio.getHoldingForPlayer(playerId)
                .orElseThrow(() -> new InsufficientEscrowException("No tokens found for player.")); // está buena esta excepción por ejemplo
        if (!holding.hasSufficientTokens(quantity)) {
            throw new InsufficientEscrowException("Not enough tokens to place SELL order."); // god excepción hermano
        }
        holding.deductTokens(quantity); // TODO un for que mire cada token y haga token.vender que devuelva un SellOrder ya creado
        portfolioRepository.save(portfolio); // TODO fijate de poner el ID del token en null, cosa que "no tenga" owner mientras se vende

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
    public void executeMarketBuy(Long userId, Long playerId, int quantity) { // TODO lo mismo de arriba, parámetro solo que venga del aModelo de request
        if (quantity <= 0) { // TODO esto también tiene que estar en el Body (o Request) que te envíen
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Portfolio buyerPortfolio = portfolioRepository.findByUserId(userId) // TODO xd
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND_MESSAGE + userId));

        // Sweep the open SELL book (cheapest first), ignoring the taker's own resting orders.
        List<P2POrder> asks = orderRepository.findOpenSellOrders(playerId).stream()
                .filter(order -> !order.belongsTo(userId))
                .toList();

        FillPlan plan = FillPlan.build(quantity, asks); // TODO sería eso de cambiar el id de los tokens por el comprador :'v
        if (!plan.isFullyFilled()) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to buy " + quantity + " tokens for player " + playerId);
        }

        // Taker pays up front at each maker's price; the domain guards the buyer's funds.
        buyerPortfolio.deductCredits(plan.totalCost()); // TODO esto es lógica de modelo, tendría que ser alguna especie de buyer.buyTokens(User seller)
        TokenHolding buyerHolding = buyerPortfolio.getOrCreateHoldingForPlayer(playerId);

        Map<Long, Portfolio> sellerPortfolios = new HashMap<>(); // TODO sería eso de cambiar el id de los tokens por el comprador :'v
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
    public void cancelOrder(UUID orderId, Long userId) { // TODO esto no lo hagas todavía, centrate en compra y venta
        P2POrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId)); // buen error

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only cancel your own orders."); // TODO tendría que ser exception de service
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Only OPEN orders can be cancelled."); // TODO lo mismo
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
