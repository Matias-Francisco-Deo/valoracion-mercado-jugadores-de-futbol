package com.overcode.service.impl;

import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.model.venta.*;
import com.overcode.model.venta.orden.ResumenOrdenesDeVenta;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.exception.venta.ExisteOrdenAbiertaParaTokenException;
import com.overcode.service.interfaces.TokenMarketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TokenMarketServiceImpl implements TokenMarketService {

    private final OrdenDeCompraRepository ordenDeCompraRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final TokenRepository tokenRepository;
    private final TransactionRepository transactionRepository;

    public TokenMarketServiceImpl(OrdenDeCompraRepository ordenDeCompraRepository,
                                  UserRepository userRepository,
                                  PlayerRepository playerRepository,
                                  TokenRepository tokenRepository,
                                  TransactionRepository transactionRepository) {
        this.ordenDeCompraRepository = ordenDeCompraRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.tokenRepository = tokenRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public List<OrdenDeCompra> sell(SellPost command) {
        Long userId = command.userId();
        Long playerId = command.playerId();
        int quantity = command.quantity();

        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + playerId));

        User seller = userRepository.recuperar(userId)
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + userId));

        MotorDeMercado motorDeMercado = new MotorDeMercado();
        List<OrdenDeCompra> orders = motorDeMercado.venderTokens(seller, player, quantity);

        for (OrdenDeCompra order : orders) {
            Long tokenId = order.getToken().getId();

            if (ordenDeCompraRepository.existsOpenOrderByTokenId(tokenId)) {
                throw new ExisteOrdenAbiertaParaTokenException("Ya existe una orden abierta para el token: " + tokenId);
            }
        }

        userRepository.guardar(seller);
        playerRepository.guardar(player);

        return ordenDeCompraRepository.saveAll(orders);
    }

    @Override
    @Transactional
    public void buy(BuyOrder buyOrder) {
        Long buyerId = buyOrder.getOwner().getId();
        Long playerId = buyOrder.getPlayer().getId();
        int quantity = buyOrder.getQuantity();

        User buyer = userRepository.recuperar(buyerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("El comprador no existe: " + buyerId));

        if (!playerRepository.existsById(playerId)){throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);}

        // Buscar órdenes de venta abiertas excluyendo las del propio comprador
        List<OrdenDeCompra> openOrders = ordenDeCompraRepository.findOpenSellOrders(playerId,buyerId);

        MotorDeMercado motorDeMercado = new MotorDeMercado(); // TODO testear?
        TransaccionCompra transaccionCompra = motorDeMercado.comprarOrdenes(openOrders, quantity, buyer);

        userRepository.guardar(buyer);
        List<OrdenDeCompra> ordenesDeCompra = transaccionCompra.ordenesCompra();

        ordenesDeCompra.stream().map(OrdenDeCompra::getOwner).distinct().forEach(userRepository::guardar);
        ordenDeCompraRepository.saveAll(ordenesDeCompra);
        transactionRepository.guardarTodos(transaccionCompra.transaccionesFinancieras());
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        OrdenDeCompra order = ordenDeCompraRepository.recuperarPorId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Órden no encontrada: " + orderId));

        order.cancel(userId);
        ordenDeCompraRepository.save(order);

        // Devolver el token al vendedor
        tokenRepository.setTokenOwner(order.getToken().getId(), userId);
    }

    @Override
    public Page<ResumenOrdenesDeVenta> buscarOrdersSellSummaryAbiertas(Long userId, String nombre, Pageable pageable) {
        return ordenDeCompraRepository.buscarOrdenesDeVentaAbiertas(userId, nombre, pageable);
    }

    @Override
    public Page<ResumenOrdenesDeVenta> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId, String nombre, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntidadNoEncontradaException("El usuario no existe");
        }

        return ordenDeCompraRepository.buscarOrdenesDeVentaDeUser(userId, nombre,pageable);
    }

}
