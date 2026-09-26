package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<PlayerJPADTO> findByNameIgnoreCase(String name);

    String FIND_BY_CLUBNAME = "unaccent(LOWER(p.club_name)) LIKE unaccent(CONCAT(LOWER(CAST(:clubName AS text)), '%'))";

    String FIND_BY_LEAGUE = "unaccent(LOWER(p.league)) LIKE unaccent(CONCAT(LOWER(CAST(:league AS text)), '%'))";

    String FIND_BY_FILTRO_QUERY =
            "FROM players p " +
                    "WHERE " + FIND_BY_CLUBNAME + " " +
                    "AND " + FIND_BY_LEAGUE ;

    @Query(
            value = "SELECT * " + FIND_BY_FILTRO_QUERY,
            countQuery = "SELECT COUNT(*) " + FIND_BY_FILTRO_QUERY,
            nativeQuery = true)
    List<PlayerJPADTO> listarJugadores(
            @Param("clubName") String clubName,
            @Param("league") String league
    );

// TODO hay que hacer un filtrado por posición, no todavía

    @Modifying
    @Transactional
    @Query(
            "update player p " +
                    "set p.currentPrice=:currentPrice," +
                    "p.assists = :assists, " +
                    "p.name = :name," +
                    "p.goals = :goals," +
                    "p.clubName = :clubName," +
                    "p.shotsOnTarget = :shotsOnTarget," +
                    "p.passes = :passes," +
                    "p.interceptions = :interceptions," +
                    "p.tackles = :tackles," +
                    "p.keyPasses = :keyPasses," +
                    "p.rating = :rating," +
                    "p.successfulDribbles = :successfulDribbles " +
                    "where p.externalId = :externalId"
    )
    void updateWithExternalId(
            @Param("externalId") Long externalId,
            @Param("name") String name,
            @Param("goals") Integer goals,
            @Param("currentPrice") Integer currentPrice,
            @Param("assists") Integer assists,
            @Param("clubName") String clubName,
            @Param("shotsOnTarget") Integer shotsOnTarget,
            @Param("passes") Integer passes,
            @Param("interceptions") Integer interceptions,
            @Param("tackles") Integer tackles,
            @Param("keyPasses") Integer keyPasses,
            @Param("rating") Double rating,
            @Param("successfulDribbles") Integer successfulDribbles
    );

    boolean existsByExternalId(Long externalId);

    Optional<PlayerJPADTO> findByExternalId(Long externalId);

    @Query(
            "from player p order by p.rating desc limit 5"
    )
    List<PlayerJPADTO> listarTop5JugadoresPorRating();
}