package com.overcode.service.impl;

import com.overcode.model.*;
import com.overcode.model.orden.OrderSellSummary;
import com.overcode.model.token.Token;
import com.overcode.model.transaction.Transaction;
import com.overcode.model.transaction.TransactionType;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.interfaces.P2POrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class P2POrderServiceImpl implements P2POrderService {

    private final SellOrderRepository sellOrderRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final TokenRepository tokenRepository;
    private final TransactionRepository transactionRepository;

    public P2POrderServiceImpl(SellOrderRepository sellOrderRepository,
                               UserRepository userRepository,
                               PlayerRepository playerRepository,
                               TokenRepository tokenRepository,
                               TransactionRepository transactionRepository) {
        this.sellOrderRepository = sellOrderRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.tokenRepository = tokenRepository;
        this.transactionRepository = transactionRepository;
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

        List<SellOrder> orders = seller.sellTokens(player,quantity);

        userRepository.guardar(seller);
        playerRepository.guardar(player);

        return sellOrderRepository.saveAll(orders);
    }

    @Override
    @Transactional
    public void executeMarketBuy(BuyOrder buyOrder) {
        Long buyerId = buyOrder.getOwner().getId();
        Long playerId = buyOrder.getPlayer().getId();
        int quantity = buyOrder.getQuantity();

        User buyer = userRepository.recuperar(buyerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + buyerId));

        if (!playerRepository.existeById(playerId)){throw new EntidadNoEncontradaException("Player not found: " + playerId);}

        // Buscar órdenes de venta abiertas excluyendo las del propio comprador
        List<SellOrder> openOrders = sellOrderRepository.findOpenSellOrders(playerId).stream() // TODO esto tiene que ser un método de repository que lo busque directo de la DB
                .filter(order -> !order.belongsTo(buyerId))
                .toList();

        if (openOrders.size() < quantity) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to buy " + quantity + " tokens for player " + playerId);
        }

        List<SellOrder> ordersToFill = openOrders.subList(0, quantity);

        List<Transaction> transactions = buyer.buyTokens(ordersToFill);

        userRepository.guardar(buyer);
        ordersToFill.stream().map(SellOrder::getOwner).distinct().forEach(userRepository::guardar);
        sellOrderRepository.saveAll(ordersToFill);
        transactionRepository.guardarTodos(transactions);
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
        tokenRepository.setTokenOwner(order.getToken().getId(), userId);
    }

    @Override
    public Page<OrderSellSummary> buscarOrdersSellSummaryAbiertas(Long userId, String nombre, Pageable pageable) {
        if(userId != null){
            return sellOrderRepository.buscarOrdersSellAbiertasExcluyendoUsuario(userId,nombre,pageable);
        }
        return sellOrderRepository.buscarOrdersSellSummaryAbiertas(nombre,pageable);
    }

    @Override
    public Page<OrderSellSummary> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId, String nombre, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntidadNoEncontradaException("El usuario no existe");
        }

        return sellOrderRepository.buscarOrdersSellSummaryAbiertasPorUsuario(userId, nombre,pageable);
    }

}
