package com.overcode.persistence.repository;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import com.overcode.model.Team;
import com.overcode.persistence.repository.dao.jpa.PlayerGameDataDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerGameDataRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConGameData;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "scraper.api.key=test-api-key"
})
@ActiveProfiles("test")
class PlayerGameDataRepositoryTest {

    @Autowired
    private PlayerGameDataRepository playerGameDataRepository;

    @Autowired
    private PlayerGameDataDAOJPA playerGameDataDAOJPA;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    @Transactional
    void guardarPersisteTodosLosCamposEstadisticos() {
        PlayerGameData playerGameData = new PlayerGameData(
                731,
                619,
                827,
                623,
                914,
                838,
                9.91,
                "Forward"
        );
        Player player = getJugadorConGameData("Jorgelín", playerGameData);
        playerGameData.setPlayer(player);

        Team teamGuardado = teamRepository.guardar(getTeamConJugadores(List.of(player)));
        Player playerGuardado = teamGuardado.getPlayers().getFirst();
        PlayerGameData resultado = playerGuardado.getPlayerGameData();
        playerGameDataDAOJPA.flush();

        assertNotNull(resultado.getId());
        assertEquals(731, resultado.getGoals());
        assertEquals(619, resultado.getAssists());
        assertEquals(827, resultado.getShotsOnTarget());
        assertEquals(623, resultado.getKeyPasses());
        assertEquals(914, resultado.getTackles());
        assertEquals(838, resultado.getSuccessfulDribbles());
        assertEquals(9.91,resultado.getRating());

        Optional<PlayerGameData> recuperado = playerGameDataRepository.recuperar(resultado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(731, recuperado.get().getGoals());
        assertEquals(619, recuperado.get().getAssists());
        assertEquals(827, recuperado.get().getShotsOnTarget());
        assertEquals(623, recuperado.get().getKeyPasses());
        assertEquals(914, recuperado.get().getTackles());
        assertEquals(838, recuperado.get().getSuccessfulDribbles());
        assertEquals(9.91, recuperado.get().getRating());
    }

    @Test
    @Transactional
    void guardarPersisteTodosLosCamposEstadisticosYRecuperaElRegistro() {
        PlayerGameData playerGameData = new PlayerGameData(
                731,
                619,
                827,
                623,
                914,
                838,
                9.91,
                "Midfielder"
        );
        Player player = getJugadorConGameData("Jorgelín", playerGameData);
        playerGameData.setPlayer(player);

        Team teamGuardado = teamRepository.guardar(getTeamConJugadores(List.of(player)));
        Player playerGuardado = teamGuardado.getPlayers().getFirst();
        PlayerGameData resultado = playerGuardado.getPlayerGameData();
        playerGameDataDAOJPA.flush();

        Optional<PlayerGameData> recuperado = playerGameDataRepository.recuperar(resultado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(731, recuperado.get().getGoals());
        assertEquals(619, recuperado.get().getAssists());
        assertEquals(827, recuperado.get().getShotsOnTarget());
        assertEquals(623, recuperado.get().getKeyPasses());
        assertEquals(914, recuperado.get().getTackles());
        assertEquals(838, recuperado.get().getSuccessfulDribbles());
        assertEquals(9.91, recuperado.get().getRating());
        assertEquals("Midfielder", recuperado.get().getPosition());
    }

    @Test
    @Transactional
    void recuperarDevuelveVacioCuandoElRegistroNoExiste() {
        assertTrue(playerGameDataRepository.recuperar(-1L).isEmpty());
    }
}
