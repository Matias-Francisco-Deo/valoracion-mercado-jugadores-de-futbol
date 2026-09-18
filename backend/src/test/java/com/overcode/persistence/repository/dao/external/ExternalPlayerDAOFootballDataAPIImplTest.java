package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.FootballDataPlayerDraftDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExternalPlayerDAOFootballDataAPIImplTest {

    @Autowired
    private ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImpl;

    @Test
    void encuentraTodosLosJugadores() {
        Optional<List<FootballDataPlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();
        if (jugadores.isEmpty()) {
            return; // TODO cómo testear estos casos?
        }

        assertFalse(jugadores.get().isEmpty());
    }

    @Test
    void encuentraTodosLosJugadoresConDatos() {
        Optional<List<FootballDataPlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

        if (jugadores.isEmpty()) {
            return; // TODO cómo testear estos casos?
        }

        jugadores.get().forEach(jugador -> {
            assertNotNull(jugador.name());
            assertNotNull(jugador.league());
        });
    }

    @Test
    void encuentraTodasLasLigasConIds() {
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

    @Disabled
    @Test
    void noEncuentraLigasPorFalloDeApiEntoncesDaEmpty() {
        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImpl.getCompetitions();

        assertTrue(ligas.isEmpty());
    }

}
