package com.overcode.persistence.repository.interfaces;

import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrdenDeCompraRepository {

    OrdenDeVenta save(OrdenDeVenta order);

    List<OrdenDeVenta> saveAll(List<OrdenDeVenta> orders);

    Optional<OrdenDeVenta> recuperarPorId(Long id);

    boolean existsOpenOrderByTokenId(Long tokenId);

    List<OrdenDeVenta> findOpenSellOrders(Long playerId, Long buyerId);

    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaAbiertas(Long userId, String nombre, Pageable pageable);

    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaDeUser(Long userId, String nombre, Pageable pageable);
}
