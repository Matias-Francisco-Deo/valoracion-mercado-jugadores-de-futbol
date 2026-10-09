package com.overcode.service.impl;

import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;
import com.overcode.persistence.repository.interfaces.OrderRepository;
import com.overcode.service.interfaces.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository){this.orderRepository = orderRepository;}

    @Override
    public Page<TokenForSaleProjection> searchTokensForSale(String playerName, Pageable pageable){
        return orderRepository.searchTokensForSale(playerName,pageable);
    }
}
