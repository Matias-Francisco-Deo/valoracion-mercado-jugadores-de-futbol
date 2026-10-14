package com.overcode.persistence.repository.interfaces;

import com.overcode.persistence.dto.jpa.SummaryTokenPlayerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TokenRepository {

    Page<SummaryTokenPlayerDTO> findPortfolioTokens(Long userId, boolean forSale, Pageable pageable);
}