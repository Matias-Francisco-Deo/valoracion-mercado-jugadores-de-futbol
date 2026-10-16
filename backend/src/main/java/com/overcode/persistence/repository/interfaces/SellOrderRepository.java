package com.overcode.persistence.repository.interfaces;

import com.overcode.model.SellOrder;

import java.util.List;
import java.util.Optional;

public interface SellOrderRepository {

    SellOrder save(SellOrder order);

    List<SellOrder> saveAll(List<SellOrder> orders);

    Optional<SellOrder> findById(Long id);

    List<SellOrder> findOpenSellOrders(Long playerId);
}
