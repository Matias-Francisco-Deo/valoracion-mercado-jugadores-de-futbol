package com.overcode.persistence.repository;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExternalPlayerRepositoryTest {

    @Autowired
    private ExternalPlayerRepository externalPlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestService testService;
    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    @Disabled("Use to test manually given its connected to an external API")
    void listarJugadoresMuestraUnJugador() {
        Optional<List<PlayerDraftDTO>> optionalJugador = externalPlayerRepository.listarJugadores(1);

        if (optionalJugador.isEmpty()) return;

        assertNotNull(optionalJugador.get());
        assertNotNull(optionalJugador.get().getFirst().team());
        assertNotNull(optionalJugador.get().getFirst().name());

    }

    @Test
    @Disabled("Use to test manually given its connected to an external API")
    void listarJugadoresMuestraTantosJugadoresComoSeLePida() {
        Optional<List<PlayerDraftDTO>> optionalJugador = externalPlayerRepository.listarJugadores(3);

        if (optionalJugador.isEmpty()) return;

        assertEquals(3, optionalJugador.get().size());

    }

    @Test
    @Disabled("Use to test manually given its connected to an external API")
    void seObtienenLosDatosCompletosDeUnJugador() {
        Optional<List<PlayerDraftDTO>> optionalJugador = externalPlayerRepository.listarJugadores(1);

        if (optionalJugador.isEmpty()) return;

        Optional<Player> jugador = externalPlayerRepository.getDatosDeJugador(optionalJugador.get().getFirst());

        if (jugador.isEmpty()) return;

        assertNotNull(jugador.get());
        assertNotNull(jugador.get().getName());
        assertNotNull(jugador.get().getExternalId());
        assertNotNull(jugador.get().getPlayerGameData());
        assertNotNull(jugador.get().getTeam());
    }

    @Test
    void seUpserteaUnJugadorInexistenteYSeGuarda() {
        Player pepito = getJugadorConNombre("Pepito");
        pepito.setExternalId(5L);
        Player jugadorRecuperado = externalPlayerRepository.upsertPlayerByExternalId(pepito);

        assertNotNull(jugadorRecuperado.getId());
        assertEquals(5L, jugadorRecuperado.getExternalId().longValue());
    }

    @Test
    void seUpserteaUnJugadorPreexistentePorIdExternaYSeActualiza() {
        Player pepito = getJugadorConNombre("Pepito");
        pepito.setExternalId(5L);

        Team team = teamRepository.guardar(pepito.getTeam());
        pepito.setTeam(team);
        playerRepository.guardar(pepito);

        pepito.getPlayerGameData().setRating(9.0);

        Player jugadorRecuperado = externalPlayerRepository.upsertPlayerByExternalId(pepito);

        assertNotNull(jugadorRecuperado.getId());
        assertEquals(9.0, jugadorRecuperado.getPlayerGameData().getRating(), 0.1);
    }
}
