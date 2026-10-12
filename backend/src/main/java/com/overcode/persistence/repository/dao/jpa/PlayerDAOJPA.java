package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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



    boolean existsByExternalId(Long externalId);

    @Query(
            "from player p order by p.playerGameData.rating desc limit 5"
    )
    List<PlayerJPADTO> listarTop5JugadoresPorRating();
}