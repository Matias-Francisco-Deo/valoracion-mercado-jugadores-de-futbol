package com.overcode.persistence.repository;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;
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
    void listarJugadoresMuestraUnEquipo() {
        Optional<List<TeamDraftDTO>> optionalTeams = externalPlayerRepository.listarEquiposDeJugadores(1);

        if (optionalTeams.isEmpty()) return;

        assertNotNull(optionalTeams.get());
        assertNotNull(optionalTeams.get().getFirst().league());
        assertNotNull(optionalTeams.get().getFirst().name());
        assertNotNull(optionalTeams.get().getFirst().players());

    }

    @Test
    @Disabled("Use to test manually given its connected to an external API")
    void listarJugadoresMuestraTantosEquiposComoSeLePida() {
        Optional<List<TeamDraftDTO>> optionalTeams = externalPlayerRepository.listarEquiposDeJugadores(3);

        if (optionalTeams.isEmpty()) return;

        assertEquals(3, optionalTeams.get().size());

    }

    @Test
    @Disabled("Use to test manually given its connected to an external API")
    void seObtienenLosDatosCompletosDeJugadoresDeUnEquipo() {
        Optional<List<TeamDraftDTO>> optionalTeams = externalPlayerRepository.listarEquiposDeJugadores(1);

        if (optionalTeams.isEmpty()) return;

        Optional<List<Team>> equipos = externalPlayerRepository.getDatosDeEquipos(optionalTeams.get());

        if (equipos.isEmpty()) return;

        assertNotNull(equipos.get());
        equipos.get().getFirst().getPlayers().forEach(player -> {
            assertNotNull(player.getName());
            assertNotNull(player.getExternalId());
            assertNotNull(player.getPlayerGameData());
            assertNotNull(player.getTeam());
        });
    }

    @Test
    @Disabled("Este comportamiento ya no se espera de update")
    void seUpserteaUnJugadorInexistenteYSeGuarda() {
        Player pepito = getJugadorConNombre("Pepito");
        pepito.setExternalId(5L);
        Player jugadorRecuperado = externalPlayerRepository.updatePlayerByExternalId(pepito);

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

        Player jugadorRecuperado = externalPlayerRepository.updatePlayerByExternalId(pepito);

        assertNotNull(jugadorRecuperado.getId());
        assertEquals(9.0, jugadorRecuperado.getPlayerGameData().getRating(), 0.1);
    }
}
