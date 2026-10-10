package com.overcode.persistence.repository.impl;

import com.overcode.model.Portfolio;
import com.overcode.persistence.dto.jpa.PortfolioJPADTO;
import com.overcode.persistence.repository.dao.jpa.PortfolioDAOJPA;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PortfolioRepositoryImpl implements PortfolioRepository {

    private final PortfolioDAOJPA portfolioDAOJPA;

    public PortfolioRepositoryImpl(PortfolioDAOJPA portfolioDAOJPA) {
        this.portfolioDAOJPA = portfolioDAOJPA;
    }

    @Override
    public Optional<Portfolio> findByUserId(Long userId) {
        return portfolioDAOJPA.findByUserId(userId).map(PortfolioJPADTO::aModelo);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioJPADTO dto = PortfolioJPADTO.desdeModelo(portfolio);
        PortfolioJPADTO saved = portfolioDAOJPA.save(dto);
        return saved.aModelo();
    }

    @Override
    public List<Portfolio> findAll() {
        return portfolioDAOJPA.findAll().stream()
                .map(PortfolioJPADTO::aModelo)
                .collect(Collectors.toList());
    }
}

