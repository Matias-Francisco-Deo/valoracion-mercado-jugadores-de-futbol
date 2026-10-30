package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import com.overcode.persistence.dto.jpa.OrdenDeVentaJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellOrderDAOJPA extends JpaRepository<OrdenDeVentaJPADTO, Long> {

    boolean existsByToken_IdAndStatus(Long tokenId, OrderStatus status);

    //trae las ordenes con estado recibido de un jugador, excluyendo al comprador
    List<OrdenDeVentaJPADTO> findByPlayer_IdAndStatusAndOwner_IdNotOrderByPriceAscCreatedAtAsc(
            Long playerId, OrderStatus status, Long buyerId);

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
                    "AND (:userId IS NULL OR s.user_id <> :userId)" +
                    "GROUP BY s.player_id, p.name " +
                    "ORDER BY p.name ASC",
            nativeQuery = true)
    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaAbiertas(
            @Param("userId") Long userId,
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
    Page<ResumenOrdenesDeVenta> buscarOrdenesDeVentaDeUser(
            @Param("userId") Long userId,
            @Param("nombre") String nombre,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
        UPDATE sell_orders so
        SET price = p.current_price
        FROM players p
        WHERE so.player_id = p.id
          AND so.status = 'OPEN'
        """, nativeQuery = true)
    void actualizarPreciosOrdenesAbiertas();
}
