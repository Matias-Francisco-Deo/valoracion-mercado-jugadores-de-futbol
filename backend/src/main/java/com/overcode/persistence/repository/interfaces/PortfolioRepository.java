package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Portfolio;
import java.util.List;
import java.util.Optional;

public interface PortfolioRepository {
    Optional<Portfolio> findByUserId(Long userId);
    Portfolio save(Portfolio portfolio);
    List<Portfolio> findAll();
}
