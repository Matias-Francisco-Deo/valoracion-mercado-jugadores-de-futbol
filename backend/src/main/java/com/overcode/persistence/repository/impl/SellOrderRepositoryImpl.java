package com.overcode.persistence.repository.impl;

import com.overcode.model.orden.OrderSellSummary;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import com.overcode.persistence.dto.jpa.SellOrderJPADTO;
import com.overcode.persistence.repository.dao.jpa.SellOrderDAOJPA;
import com.overcode.persistence.repository.interfaces.SellOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class SellOrderRepositoryImpl implements SellOrderRepository {

    private final SellOrderDAOJPA dao;

    public SellOrderRepositoryImpl(SellOrderDAOJPA dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public SellOrder save(SellOrder order) {
        return dao.save(SellOrderJPADTO.desdeModelo(order)).aModelo();
    }

    @Override
    @Transactional
    public List<SellOrder> saveAll(List<SellOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        List<SellOrderJPADTO> entities = orders.stream()
                .map(SellOrderJPADTO::desdeModelo)
                .toList();
        return dao.saveAll(entities).stream()
                .map(SellOrderJPADTO::aModelo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellOrder> findById(Long id) {
        return dao.findById(id).map(SellOrderJPADTO::aModelo);
    }

    @Override
    @Transactional(readOnly = true)//trae las ordenes abiertas de un jugador, excluyendo al comprador
    public List<SellOrder> findOpenSellOrders(Long playerId,Long buyerId) {
        return dao.findByPlayer_IdAndStatusAndOwner_IdNotOrderByPriceAscCreatedAtAsc(playerId, OrderStatus.OPEN,buyerId)
                .stream()
                .map(SellOrderJPADTO::aModelo)
                .toList();
    }

    @Override
    public Page<OrderSellSummary> buscarOrdersSellSummaryAbiertas(String nombre, Pageable pageable) {
        return dao.buscarOrdersSellAbiertas(nombre, pageable);
    }

    @Override
    public Page<OrderSellSummary> buscarOrdersSellAbiertasExcluyendoUsuario(Long userId, String nombre, Pageable pageable){
        return dao.buscarOrdersSellAbiertasExcluyendoUsuario(userId, nombre, pageable);
    }

    @Override//recuoera pagina de summary de ordenes abiertas de un usuario
    public Page<OrderSellSummary> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId, String nombre, Pageable pageable) {
        return dao.buscarOrdersSellAbiertasPorUsuario(userId, nombre, pageable);
    }

}
