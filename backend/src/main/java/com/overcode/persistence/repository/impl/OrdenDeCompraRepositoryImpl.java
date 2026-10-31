package com.overcode.persistence.repository.impl;

import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import com.overcode.persistence.dto.jpa.OrdenDeVentaJPADTO;
import com.overcode.persistence.repository.dao.jpa.SellOrderDAOJPA;
import com.overcode.persistence.repository.interfaces.OrdenDeCompraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdenDeCompraRepositoryImpl implements OrdenDeCompraRepository {

    private final SellOrderDAOJPA dao;

    public OrdenDeCompraRepositoryImpl(SellOrderDAOJPA dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public OrdenDeVenta save(OrdenDeVenta order) {
        return dao.save(OrdenDeVentaJPADTO.desdeModelo(order)).aModelo();
    }

    @Override
    @Transactional
    public List<OrdenDeVenta> saveAll(List<OrdenDeVenta> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        List<OrdenDeVentaJPADTO> entities = orders.stream()
                .map(OrdenDeVentaJPADTO::desdeModelo)
                .toList();
        return dao.saveAll(entities).stream()
                .map(OrdenDeVentaJPADTO::aModelo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenDeVenta> recuperarPorId(Long id) {
        return dao.findById(id).map(OrdenDeVentaJPADTO::aModelo);
    }

    @Override
    public boolean existsOpenOrderByTokenId(Long tokenId) {
        return dao.existsByToken_IdAndStatus(tokenId, OrderStatus.OPEN);
    }

    @Override
    @Transactional(readOnly = true)//trae las ordenes abiertas de un jugador, excluyendo al comprador
    public List<OrdenDeVenta> findOpenSellOrders(Long playerId, Long buyerId) {
        return dao.findByPlayer_IdAndStatusAndOwner_IdNotOrderByPriceAscCreatedAtAsc(playerId, OrderStatus.OPEN,buyerId)
                .stream()
                .map(OrdenDeVentaJPADTO::aModelo)
                .toList();
    }

    @Override//recupera pagina de summary de ordenes abiertas excluyendo al usuario logueado
    public Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaAbiertas(Long userId, String nombre, Pageable pageable) {
        return dao.buscarOrdenesDeVentaAbiertas(userId, nombre, pageable);
    }


    @Override//recupera pagina de summary de ordenes abiertas de un usuario
    public Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaDeUser(Long userId, String nombre, Pageable pageable) {
        return dao.buscarOrdenesDeVentaDeUser(userId, nombre, pageable);
    }

    @Override
    public List<OrdenDeVenta> recuperarOrdenesDeVentaAbiertasDelUser(Long playerId,Long userId){
        return dao.findAllByPlayer_IdAndOwner_IdAndStatusOrderByCreatedAtAsc(playerId,userId,OrderStatus.OPEN)
                .stream()
                .map(OrdenDeVentaJPADTO::aModelo)
                .toList();
    }

    @Override
    public void actualizarPreciosOrdenesAbiertas() {
        dao.actualizarPreciosOrdenesAbiertas();
    }
}
