package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.persistence.dto.jpa.TokenJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenDAOJPA extends JpaRepository<TokenJPADTO, Long> {

    List<TokenJPADTO> findByOwnerIdAndPlayerId(Long ownerId, Long playerId);

    @Modifying
    @Query("UPDATE token t SET t.owner.id = :newOwnerId WHERE t.id = :tokenId")
    void updateOwnerId(@Param("tokenId") Long tokenId, @Param("newOwnerId") Long newOwnerId);

    @Modifying
    @Query("UPDATE token t SET t.owner = null WHERE t.id = :tokenId")
    void clearOwner(@Param("tokenId") Long tokenId);

    @Query(
            value = """
            SELECT
                p.id AS "idPlayer",
                p.name AS "playerName",
                COUNT(t.id)::integer AS quantity,
                p.current_price::double precision AS "pricePerToken"
            FROM tokens t
            INNER JOIN players p ON p.id = t.player_id
            WHERE t.owner_id = :ownerId
              AND (
                  :nombre IS NULL
                  OR TRIM(:nombre) = ''
                  OR unaccent(LOWER(p.name))
                     LIKE unaccent(CONCAT('%', LOWER(CAST(:nombre AS text)), '%'))
              )
            GROUP BY p.id, p.name, p.current_price
            ORDER BY p.name ASC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT p.id)
            FROM tokens t
            INNER JOIN players p ON p.id = t.player_id
            WHERE t.owner_id = :ownerId
              AND (
                  :nombre IS NULL
                  OR TRIM(:nombre) = ''
                  OR unaccent(LOWER(p.name))
                     LIKE unaccent(CONCAT('%', LOWER(CAST(:nombre AS text)), '%'))
              )
            """,
            nativeQuery = true
    )
    Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(
            @Param("ownerId") Long userId,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}
