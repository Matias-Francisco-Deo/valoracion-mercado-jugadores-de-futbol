package com.overcode.persistence.repository.interfaces;

import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.orden.ResumenOrdenesDeVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SellOrderRepository {

    OrdenDeCompra save(OrdenDeCompra order);

    List<OrdenDeCompra> saveAll(List<OrdenDeCompra> orders);

    Optional<OrdenDeCompra> findById(Long id);

    boolean existsOpenOrderByTokenId(Long tokenId);

    List<OrdenDeCompra> findOpenSellOrders(Long playerId, Long buyerId);

    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaAbiertas(Long userId, String nombre, Pageable pageable);

    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaDeUser(Long userId, String nombre, Pageable pageable);
}
