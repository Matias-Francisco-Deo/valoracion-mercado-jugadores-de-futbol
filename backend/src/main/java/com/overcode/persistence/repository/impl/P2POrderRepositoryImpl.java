package com.overcode.persistence.repository.impl;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.persistence.dto.jpa.P2POrderJPADTO;
import com.overcode.persistence.repository.dao.jpa.P2POrderDAOJPA;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class P2POrderRepositoryImpl implements P2POrderRepository {

    private final P2POrderDAOJPA dao;

    public P2POrderRepositoryImpl(P2POrderDAOJPA dao) {
        this.dao = dao;
    }

    @Override
    public P2POrder save(P2POrder order) {
        P2POrderJPADTO dto = P2POrderJPADTO.desdeModelo(order);
        return dao.save(dto).aModelo();
    }

    @Override
    public List<P2POrder> saveAll(List<P2POrder> orders) {
        List<P2POrderJPADTO> dtos = orders.stream().map(P2POrderJPADTO::desdeModelo).toList();
        return dao.saveAll(dtos).stream().map(P2POrderJPADTO::aModelo).toList();
    }

    @Override
    public Optional<P2POrder> findById(UUID id) {
        return dao.findById(id).map(P2POrderJPADTO::aModelo);
    }

    @Override
    public List<P2POrder> findOpenSellOrders(Long playerId) {
        return dao.findByPlayerIdAndStatusAndTypeOrderByPricePerTokenAscCreatedAtAsc(
                        playerId, OrderStatus.OPEN, OrderType.SELL)
                .stream().map(P2POrderJPADTO::aModelo).toList();
    }
}
