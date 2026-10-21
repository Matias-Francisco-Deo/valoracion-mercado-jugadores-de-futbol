package com.overcode.service.impl;

import com.overcode.model.*;
import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.SellOrderRepository;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.interfaces.P2POrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class P2POrderServiceImpl implements P2POrderService {

    private final SellOrderRepository sellOrderRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final TokenRepository tokenRepository;

    public P2POrderServiceImpl(SellOrderRepository sellOrderRepository,
                               UserRepository userRepository,
                               PlayerRepository playerRepository,
                               TokenRepository tokenRepository) {
        this.sellOrderRepository = sellOrderRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public List<SellOrder> createSellOrders(SellPost command) {
        Long userId = command.userId();
        Long playerId = command.playerId();
        int quantity = command.quantity();

        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + playerId));

        User seller = userRepository.recuperar(userId)
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + userId));

        List<Token> tokensToSell = seller.releaseTokensForSale(playerId, quantity);
        userRepository.guardar(seller);

        int price = player.getCurrentPrice();
        List<SellOrder> orders = tokensToSell.stream() // TODO esto tiene que ser un método de modelo
                .map(token -> {
                    tokenRepository.clearTokenOwner(token.getId());
                    return token.vender(price);
                })
                .toList();

        return sellOrderRepository.saveAll(orders);
    }

    @Override
    @Transactional
    public void executeMarketBuy(BuyOrder buyOrder) {
        Long buyerId = buyOrder.getUserId();
        Long playerId = buyOrder.getPlayerId();
        int quantity = buyOrder.getQuantity();

        User buyer = userRepository.recuperar(buyerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + buyerId));

        // Buscar órdenes de venta abiertas excluyendo las del propio comprador
        List<SellOrder> openOrders = sellOrderRepository.findOpenSellOrders(playerId).stream() // TODO esto tiene que ser un método de repository que lo busque directo de la DB
                .filter(order -> !order.belongsTo(buyerId))
                .toList();

        if (openOrders.size() < quantity) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to buy " + quantity + " tokens for player " + playerId);
        }

        List<SellOrder> ordersToFill = openOrders.subList(0, quantity);
        Map<Long, User> sellersCache = new HashMap<>();

        for (SellOrder order : ordersToFill) { // TODO esto es lógica de modelo, separar
            // Cobrar al comprador (lanza InsufficientFundsException si no tiene saldo)
            buyer.debitCredits(order.getPrice());

            // Acreditar fondos al vendedor
            User seller = sellersCache.computeIfAbsent(order.getUserId(),
                    id -> userRepository.recuperar(id)
                            .orElseThrow(() -> new EntidadNoEncontradaException("Seller not found: " + id)));
            seller.creditCredits(order.getPrice());

            // Transferir el token al comprador
            tokenRepository.setTokenOwner(order.getTokenId(), buyer.getId());
            Token token = tokenRepository.findById(order.getTokenId()).orElse(null);
            if (token != null) {
                buyer.addToken(token);
            }

            // Marcar la orden como completada
            order.markCompleted();
        }

        userRepository.guardar(buyer);
        sellersCache.values().forEach(userRepository::guardar);
        sellOrderRepository.saveAll(ordersToFill);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        SellOrder order = sellOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (!order.belongsTo(userId)) {
            throw new IllegalStateException("You can only cancel your own orders.");
        }

        order.cancel();
        sellOrderRepository.save(order);

        // Devolver el token al vendedor
        tokenRepository.setTokenOwner(order.getTokenId(), userId);
    }
}
