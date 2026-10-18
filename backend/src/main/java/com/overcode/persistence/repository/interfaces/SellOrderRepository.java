package com.overcode.persistence.repository.interfaces;

import com.overcode.model.SellOrder;
import com.overcode.model.dto.OrderSellSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SellOrderRepository {

    SellOrder save(SellOrder order);

    List<SellOrder> saveAll(List<SellOrder> orders);

    Optional<SellOrder> findById(Long id);

    List<SellOrder> findOpenSellOrders(Long playerId);

    Page<OrderSellSummaryDTO> buscarOrdersSellSummaryAbiertas (String nombre, Pageable pageable);

    Page<OrderSellSummaryDTO> buscarOrdersSellAbiertasExcluyendoUsuario(Long userId, String nombre, Pageable pageable);

    Page<OrderSellSummaryDTO> buscarOrdersSellSummaryAbiertasPorUsuario(Long userId,String nombre, Pageable pageable);
}
