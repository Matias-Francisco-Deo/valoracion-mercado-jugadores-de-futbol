package com.overcode.service.interfaces;

import com.overcode.controller.dto.order.OrderBuy;
import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<TokenForSaleProjection> searchTokensForSale(String playerName, Pageable pageable);

    OrderBuy buyTokens(Long idPlayer, Integer quantity);
}
