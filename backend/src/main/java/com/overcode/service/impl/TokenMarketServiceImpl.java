package com.overcode.service.impl;

import com.overcode.model.market.MotorDeMercado;
import com.overcode.model.market.PeticionCancelacion;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.TransaccionCompra;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
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
    private final TransactionRepository transactionRepository;

    public TokenMarketServiceImpl(OrdenDeCompraRepository ordenDeCompraRepository,
                                  UserRepository userRepository,
                                  PlayerRepository playerRepository,
                                  TransactionRepository transactionRepository) {
        this.ordenDeCompraRepository = ordenDeCompraRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public List<OrdenDeVenta> sell(PeticionVenta peticionVenta) {
        Long userId = peticionVenta.user().getId();
        Long playerId = peticionVenta.player().getId();
        int quantity = peticionVenta.quantity();

        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + playerId));

        User seller = userRepository.recuperar(userId)
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + userId));

        MotorDeMercado motorDeMercado = new MotorDeMercado();
        List<OrdenDeVenta> orders = motorDeMercado.venderTokens(seller, player, quantity);

        for (OrdenDeVenta order : orders) {
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
    public void buy(OrdenDeCompra buyOrder) {
        Long buyerId = buyOrder.getOwner().getId();
        Long playerId = buyOrder.getPlayer().getId();
        int quantity = buyOrder.getQuantity();

        User buyer = userRepository.recuperar(buyerId)
                .orElseThrow(() -> new EntidadNoEncontradaException("El comprador no existe: " + buyerId));

        if (!playerRepository.existsById(playerId)){throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);}

        // Buscar órdenes de venta abiertas excluyendo las del propio comprador
        List<OrdenDeVenta> openOrders = ordenDeCompraRepository.findOpenSellOrders(playerId,buyerId);

        MotorDeMercado motorDeMercado = new MotorDeMercado();
        TransaccionCompra transaccionCompra = motorDeMercado.comprarOrdenes(openOrders, quantity, buyer);

        userRepository.guardar(buyer);
        List<OrdenDeVenta> ordenesDeCompra = transaccionCompra.ordenesCompra();

        ordenesDeCompra.stream().map(OrdenDeVenta::getOwner).distinct().forEach(userRepository::guardar);
        ordenDeCompraRepository.saveAll(ordenesDeCompra);
        transactionRepository.guardarTodos(transaccionCompra.transaccionesFinancieras());
    }

    @Override
    @Transactional
    public void cancel(PeticionCancelacion peticionCancelacion) {
        Long userId = peticionCancelacion.user().getId();
        Long playerId = peticionCancelacion.player().getId();
        int quantity = peticionCancelacion.quantity();

        if (!playerRepository.existsById(playerId)){throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);}

        User seller = userRepository.recuperar(userId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado: " + userId));

        //recupera todas las ordenes abiertas de un jugador del user
        List<OrdenDeVenta> openOrders = ordenDeCompraRepository.recuperarOrdenesDeVentaAbiertasDelUser(playerId,userId);

        MotorDeMercado motorDeMercado = new MotorDeMercado();
        motorDeMercado.cancelarOrdenes(openOrders,seller,quantity);

        userRepository.guardar(seller);
        ordenDeCompraRepository.saveAll(openOrders);
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
