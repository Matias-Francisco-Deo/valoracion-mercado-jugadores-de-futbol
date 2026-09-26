package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<PlayerJPADTO> findByNameIgnoreCase(String name);

    List<PlayerJPADTO> findAllByOrderByIdAsc();

    @Modifying
    @org.springframework.transaction.annotation.Transactional
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