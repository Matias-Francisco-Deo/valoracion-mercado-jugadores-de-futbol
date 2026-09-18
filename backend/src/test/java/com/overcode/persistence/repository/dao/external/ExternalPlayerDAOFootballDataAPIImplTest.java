package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionAreaDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.FootballDataPlayerDraftDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.TeamDraftDTO;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExternalPlayerDAOFootballDataAPIImplTest {

    public static final CompetitionDTO COMPETITION_1 = new CompetitionDTO(2014L, "Primera Division", new CompetitionAreaDTO("Spain"));

    @Autowired
    private ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImpl;

    @Test
    void encuentraTodosLosJugadores() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();
//        if (jugadores.isEmpty()) {
//            return; // TODO cómo testear estos casos?
//        }

        assertFalse(jugadores.get().isEmpty());
    }

    @Test
    void encuentraTodosLosJugadoresConDatos() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

//        if (jugadores.isEmpty()) {
//            return; // TODO cómo testear estos casos?
//        }

        jugadores.get().forEach(jugador -> {
            assertNotNull(jugador.name());
            assertNotNull(jugador.league());
        });
        assertFalse(jugadores.get().isEmpty());
    }

    @Test
    void encuentraTodasLasLigasConIds() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImpl.getCompetitions();

//        if (ligas.isEmpty()) {
//            return; // TODO cómo testear estos casos?
//        }

        ligas.get().forEach(liga -> {
            assertNotNull(liga.id());
            assertNotNull(liga.name());
        });
        assertEquals(externalPlayerDAOFootballDataAPIImpl.getLeaguesToUse().size(), ligas.get().size());
    }

    @Test
    void encuentraTodosLosEquiposDeUnaCompetencia() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<TeamDraftDTO>> equipos = externalPlayerDAOFootballDataAPIImpl.getTeamsOfCompetition(COMPETITION_1);

//        if (ligas.isEmpty()) {
//            return; // TODO cómo testear estos casos?
//        }

        equipos.get().forEach(equipo -> {
            assertNotNull(equipo.id());
            assertNotNull(equipo.name());
        });
        assertFalse(equipos.get().isEmpty());
    }

    @Disabled
    @Test
    void noEncuentraLigasPorFalloDeApiEntoncesDaEmpty() {



        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImpl.getCompetitions();

        assertTrue(ligas.isEmpty());
    }


//    @Test
//    void encuentraTodosLosJugadoresDeUnEquipo() {
//        Optional<List<FootballDataPlayerDraftDTO>> players = externalPlayerDAOFootballDataAPIImpl.getPlayersOfTeam(TEAM_1);
//
//        players.get().forEach(player -> {
//            assertNotNull(player.id());
//            assertNotNull(player.name());
//        });
//        assertFalse(players.get().isEmpty());
    }



