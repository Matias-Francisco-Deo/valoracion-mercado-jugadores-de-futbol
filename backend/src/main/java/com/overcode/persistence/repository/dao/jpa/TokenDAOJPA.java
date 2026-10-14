package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.SummaryTokenPlayerDTO;
import com.overcode.persistence.dto.jpa.TokenJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenDAOJPA extends JpaRepository<TokenJPADTO, Long> {

    @Query(
            value = """
            SELECT
                p.id AS playerId,
                p.name AS playerName,
                COUNT(tok.id) AS quantity,
                p.current_price AS currentPrice
            FROM tokens tok
            JOIN players p ON tok.player_id = p.id
            WHERE tok.owner_id = :userId
              AND tok.for_sale = :forSale
            GROUP BY
                p.id,
                p.name,
                p.current_price
            ORDER BY p.name ASC, p.id ASC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT tok.player_id
                FROM tokens tok
                WHERE tok.owner_id = :userId
                  AND tok.for_sale = :forSale
                GROUP BY tok.player_id
            ) AS portfolio_tokens
            """,
            nativeQuery = true
    )
    Page<SummaryTokenPlayerDTO> findPortfolioTokens(
            @Param("userId") Long userId,
            @Param("forSale") boolean forSale,
            Pageable pageable
    );
}