package com.overcode.persistence.repository.impl;

import com.overcode.persistence.repository.dao.jpa.TokenDAOJPA;
import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;
import com.overcode.persistence.repository.interfaces.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private TokenDAOJPA tokenDAOJPA;

    public OrderRepositoryImpl(TokenDAOJPA tokenDAOJPA) {
        this.tokenDAOJPA = tokenDAOJPA;
    }

    @Override
    public Page<TokenForSaleProjection> searchTokensForSale(String playerName,Pageable pageable){
        return tokenDAOJPA.searchTokensForSale(playerName,pageable);
    }
}
