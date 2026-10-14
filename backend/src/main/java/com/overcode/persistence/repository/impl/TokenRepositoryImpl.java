package com.overcode.persistence.repository.impl;

import com.overcode.persistence.dto.jpa.SummaryTokenPlayerDTO;
import com.overcode.persistence.repository.dao.jpa.TokenDAOJPA;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenDAOJPA tokenDAOJPA;

    public TokenRepositoryImpl(TokenDAOJPA tokenDAOJPA) {
        this.tokenDAOJPA = tokenDAOJPA;
    }

    @Override
    public Page<SummaryTokenPlayerDTO> findPortfolioTokens(Long userId, boolean forSale, Pageable pageable) {
        return tokenDAOJPA.findPortfolioTokens(userId, forSale, pageable);
    }
}
