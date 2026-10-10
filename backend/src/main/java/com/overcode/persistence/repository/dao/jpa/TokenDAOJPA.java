package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.TokenJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenDAOJPA extends JpaRepository<TokenJPADTO, Long> {

    String FIND_BY_PLAYER_NAME = "(:playerName IS NULL OR " +
                    "unaccent(LOWER(p.name)) LIKE " +
                    "unaccent(CONCAT(LOWER(CAST(:playerName AS text)), '%')))";

    String FIND_BY_FOR_SALE_QUERY =
            "FROM tokens tok " +
                    "JOIN players p ON tok.player_id = p.id " +
                    "LEFT JOIN teams t ON p.team_id = t.id " +
                    "LEFT JOIN players_game_data pgd ON pgd.player_id = p.id " +
                    "WHERE tok.for_sale = true " +
                    "AND " + FIND_BY_PLAYER_NAME;

    @Query(
            value = "SELECT " +
                    "p.id AS playerId, " +
                    "p.name AS playerName, " +
                    "COUNT(tok.id) AS tokenQuantity, " +
                    "p.current_price AS pricePerToken " +
                    FIND_BY_FOR_SALE_QUERY +
                    " GROUP BY p.id, p.name, p.current_price",
            countQuery = "SELECT COUNT(*) " + FIND_BY_FOR_SALE_QUERY,
            nativeQuery = true
    )
    Page<TokenForSaleProjection> searchTokensForSale(
            @Param("playerName") String playerName,
            Pageable pageable
    );

    @Query(value = """
        SELECT tok.id
        FROM tokens tok
        WHERE tok.player_id = :playerId
          AND tok.for_sale = true
        ORDER BY RANDOM()
        LIMIT :quantity
        FOR UPDATE
        """, nativeQuery = true)
    List<Long> findRandomTokensForUpdate(
            @Param("playerId") Long playerId,
            @Param("quantity") int quantity
    );

    @Modifying
    @Query(value = """
        UPDATE tokens
        SET owner_id = :buyerId,
            for_sale = false
        WHERE id IN (:tokenIds)
          AND player_id = :playerId
          AND for_sale = true
        """, nativeQuery = true)
    int transferTokens(
            @Param("tokenIds") List<Long> tokenIds,
            @Param("buyerId") Long buyerId
    );
}

