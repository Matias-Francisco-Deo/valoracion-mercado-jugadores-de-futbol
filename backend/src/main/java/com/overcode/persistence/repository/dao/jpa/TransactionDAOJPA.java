package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.position.PlayerPositionData;
import com.overcode.persistence.dto.jpa.TransactionJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionDAOJPA extends JpaRepository<TransactionJPADTO, Long> {
    Page<TransactionJPADTO> findAllByOwner_IdOrderByDateDesc(Long userId, Pageable pageable);

    List<TransactionJPADTO> findAllByOwner_IdOrderByDateAscIdAsc(Long userId);

    @Query(value = """
    SELECT *
    FROM (
        SELECT
            p.id AS "playerId",
            p.name AS "playerName",
            p.current_price AS "currentPrice",
            SUM(CASE WHEN t.type = 'BUY'
                THEN t.quantity ELSE 0 END)::integer AS "totalBought",
            SUM(CASE WHEN t.type = 'SELL'
                THEN t.quantity ELSE 0 END)::integer AS "totalSold",
            SUM(CASE WHEN t.type = 'BUY'
                THEN t.quantity * t.unit_price ELSE 0 END)::double precision AS "totalPurchaseAmount"
        FROM transactions t
        INNER JOIN players p ON p.id = t.player_id
        WHERE t.owner_id = :userId
        GROUP BY p.id, p.name, p.current_price
    ) AS positions
    WHERE positions."totalBought" - positions."totalSold" > 0
    """, nativeQuery = true)
    List<PlayerPositionData> findPlayerPositions(
            @Param("userId") Long userId
    );
}
