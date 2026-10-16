package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.orden.OrderStatus;
import com.overcode.model.dto.OrderSellSummaryDTO;
import com.overcode.persistence.dto.jpa.SellOrderJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellOrderDAOJPA extends JpaRepository<SellOrderJPADTO, Long> {

    List<SellOrderJPADTO> findByPlayerIdAndStatusOrderByPriceAscCreatedAtAsc(
            Long playerId, OrderStatus status);

    String FILTRO_NOMBRE =
            "(:nombre IS NULL OR TRIM(:nombre) = '' " +
                    "OR unaccent(LOWER(p.name)) " +
                    "LIKE unaccent(CONCAT('%', LOWER(CAST(:nombre AS text)), '%')))";

    String CONSULTA_RESUMEN_ORDENES =
            "FROM sell_orders s " +
                    "INNER JOIN players p ON p.id = s.player_id " +
                    "WHERE s.status = 'OPEN' " +
                    "AND " + FILTRO_NOMBRE;

    @Query(value =
            "SELECT s.player_id AS playerId, " +
                    "p.name AS playerName, " +
                    "COUNT(s.id)::integer AS quantity, " +
                    "MIN(s.price)::double precision AS unitPrice " +
                    CONSULTA_RESUMEN_ORDENES + " " +
                    "GROUP BY s.player_id, p.name " +
                    "ORDER BY p.name ASC",
            nativeQuery = true)
    Page<OrderSellSummaryDTO> buscarOrdersSellAbiertas(
            @Param("nombre") String nombre,
            Pageable pageable
    );


    @Query(value =
            "SELECT s.player_id AS playerId, " +
                    "p.name AS playerName, " +
                    "COUNT(s.id)::integer AS quantity, " +
                    "MIN(s.price)::double precision AS unitPrice " +
                    CONSULTA_RESUMEN_ORDENES + " " +
                    "AND s.user_id = :userId " +
                    "GROUP BY s.player_id, p.name " +
                    "ORDER BY p.name ASC",
            nativeQuery = true)
    Page<OrderSellSummaryDTO> buscarOrdersSellAbiertasPorUsuario(
            @Param("userId") Long userId,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}
