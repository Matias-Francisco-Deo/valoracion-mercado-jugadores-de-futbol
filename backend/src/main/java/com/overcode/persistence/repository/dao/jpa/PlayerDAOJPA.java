package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name);

    String FIND_BY_CLUBNAME = "(:teamName IS NULL OR unaccent(LOWER(t.name)) LIKE unaccent(CONCAT(LOWER(CAST(:teamName AS text)), '%')))";

    String FIND_BY_LEAGUE = "(:league IS NULL OR unaccent(LOWER(t.league)) LIKE unaccent(CONCAT(LOWER(CAST(:league AS text)), '%')))";

    String FIND_BY_FILTRO_QUERY =
            "FROM players p LEFT JOIN teams t ON p.team_id = t.id " +
                    "WHERE " + FIND_BY_CLUBNAME + " " +
                    "AND " + FIND_BY_LEAGUE ;

    @Query(
            value = "SELECT p.* " + FIND_BY_FILTRO_QUERY,
            countQuery = "SELECT COUNT(*) " + FIND_BY_FILTRO_QUERY,
            nativeQuery = true)
    Page<PlayerJPADTO> listarJugadores(
            @Param("teamName") String teamName,
            @Param("league") String league,
            Pageable pageable
    );

    // TODO hay que hacer un filtrado por posición, no todavía
    //esto modifica o se borra?
/*
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
                    "p.successfulDribbles = :successfulDribbles, " +
                    "p.position = :position " +
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
            @Param("successfulDribbles") Integer successfulDribbles,
            @Param("position") String position
    );
*/
    

    boolean existsByExternalId(Long externalId);

    Optional<PlayerJPADTO> findByExternalId(Long externalId);

    @Query(
            "from player p order by p.playerGameData.rating desc limit 5"
    )
    List<PlayerJPADTO> listarTop5JugadoresPorRating();
}