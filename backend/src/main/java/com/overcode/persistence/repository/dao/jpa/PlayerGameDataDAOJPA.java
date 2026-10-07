package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PlayerGameDataDAOJPA extends JpaRepository<PlayerGameDataJPADTO, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            "update player_game_data pgd " +
                    "set pgd.assists = :assists, " +
                    "pgd.goals = :goals," +
                    "pgd.shotsOnTarget = :shotsOnTarget," +
                    "pgd.tackles = :tackles," +
                    "pgd.keyPasses = :keyPasses," +
                    "pgd.rating = :rating," +
                    "pgd.successfulDribbles = :successfulDribbles " +
                    "where pgd.player.externalId = :externalId"
    )
    void updateWithExternalPlayerId(@Param("externalId") Long externalId,
                                    @Param("goals") Integer goals,
                                    @Param("assists") Integer assists,
                                    @Param("shotsOnTarget") Integer shotsOnTarget,
                                    @Param("tackles") Integer tackles,
                                    @Param("keyPasses") Integer keyPasses,
                                    @Param("rating") Double rating,
                                    @Param("successfulDribbles") Integer successfulDribbles);
}