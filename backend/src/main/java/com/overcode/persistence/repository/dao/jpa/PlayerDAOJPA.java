package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name);

    String FIND_BY_CLUBNAME = "unaccent(LOWER(p.team.name)) LIKE unaccent(CONCAT(LOWER(CAST(:teamName AS text)), '%'))";

    String FIND_BY_LEAGUE = "unaccent(LOWER(p.team.league)) LIKE unaccent(CONCAT(LOWER(CAST(:league AS text)), '%'))";

    String FIND_BY_FILTRO_QUERY =
            "FROM players p " +
                    "WHERE " + FIND_BY_CLUBNAME + " " +
                    "AND " + FIND_BY_LEAGUE ;

    @Query(
            value = "SELECT * " + FIND_BY_FILTRO_QUERY,
            countQuery = "SELECT COUNT(*) " + FIND_BY_FILTRO_QUERY,
            nativeQuery = true)
    List<PlayerJPADTO> listarJugadores(
            @Param("teamName") String teamName,
            @Param("league") String league
    );

    boolean existsByExternalId(Long externalId);

    Optional<PlayerJPADTO> findByExternalId(Long externalId);

    @Query(
            "from player p order by p.playerGameData.rating desc limit 5"
    )
    List<PlayerJPADTO> listarTop5JugadoresPorRating();
}