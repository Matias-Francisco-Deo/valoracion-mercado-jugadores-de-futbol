package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionAreaDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.FootballDataPlayerDraftDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.TeamDraftDTO;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExternalPlayerDAOFootballDataAPIImplTest {

    public static final CompetitionDTO COMPETITION_1 = new CompetitionDTO(2014L, "Primera Division", new CompetitionAreaDTO("Spain"));

    @Autowired
    private ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImpl;


    private final ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImplMock =
            new ExternalPlayerDAOFootballDataAPIImpl("mockKey", "http://localhost:54321");

//    @DynamicPropertySource
//    static void setupEnv(DynamicPropertyRegistry registry) {
//        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        registry.add("football-data.api-key", () -> dotenv.get("FOOTBALL_DATA_API_KEY"));
//    }

    @Test
    void encuentraTodosLosJugadores() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

        assertFalse(jugadores.get().isEmpty());
    }

    @Test
    void encuentraTodosLosJugadoresConDatos() throws InterruptedException {
        Thread.sleep(10000);
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

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

        equipos.get().forEach(equipo -> {
            assertNotNull(equipo.id());
            assertNotNull(equipo.name());
        });
        assertFalse(equipos.get().isEmpty());
    }

    @Test
    void noEncuentraLigasPorFalloDeApiEntoncesDaEmpty() {

        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.getCompetitions();

        assertTrue(ligas.isEmpty());
    }

    @Test
    void noEncuentraJugadoresPorFalloDeApiEntoncesDaEmpty() {

        Optional<List<PlayerDraftDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.listarJugadores();

        assertTrue(ligas.isEmpty());
    }

    @Test
    void noEncuentraJugadoresDeCompetenciasPorFalloDeApiEntoncesDaEmpty() {

        Optional<List<TeamDraftDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.getTeamsOfCompetition(COMPETITION_1);

        assertTrue(ligas.isEmpty());
    }



    }



