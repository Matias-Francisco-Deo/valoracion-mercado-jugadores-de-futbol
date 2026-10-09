package com.overcode.service.interfaces;

import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<TokenForSaleProjection> searchTokensForSale(String playerName, Pageable pageable);
}
