package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.PlayerDraftDTO;
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
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();
        if (jugadores.isEmpty()) {
            return; // TODO cómo testear estos casos?
        }

        assertFalse(jugadores.get().isEmpty());
    }

    @Test
    void encuentraTodosLosJugadoresConDatos() {
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

        if (jugadores.isEmpty()) {
            return; // TODO cómo testear estos casos?
        }

        jugadores.get().forEach(jugador -> {
            assertNotNull(jugador.name());
            assertNotNull(jugador.league());
        });
    }

}
