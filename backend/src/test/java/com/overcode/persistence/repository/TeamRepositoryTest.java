package com.overcode.persistence.repository;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.testUtils.TestService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static junit.framework.TestCase.*;


@SpringBootTest
class TeamRepositoryTest {

    @Autowired
    private ExternalPlayerRepository externalPlayerRepository;

    @Autowired
    private TestService testService;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        testService.eliminarJugadoresYEquipos();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    @Transactional
    void recuperarPorNombreYLigaDevuelveAUnEquipoPorEsosArgumentos() {
        Team team = new Team("team1", "league1", List.of());
        teamRepository.guardar(team);

        assertTrue(teamRepository.recuperarPorNombreYLiga("team1", "league1").isPresent());

    }

    @Test
    void recuperarPorNombreYLigaNoDevuelveNingunEquipoSiNoExiste() {
        assertTrue(teamRepository.recuperarPorNombreYLiga("team2", "league2").isEmpty());

    }

    @Test
    @Transactional
    void upsertearUnEquipoLoGuarda() {
        Team team = new Team("team2", "league2", List.of());
        teamRepository.upsertTeam(team);

        assertTrue(teamRepository.recuperarPorNombreYLiga("team2", "league2").isPresent());

    }

    @Test
    @Transactional
    void upsertearUnEquipoLoGuardaJuntoASusJugadores() {
        Player player1 = getJugadorConNombre("player1");
        Team team = new Team("team2", "league2", List.of(player1));
        player1.setTeam(team);
        teamRepository.upsertTeam(team);

        Team team1 = teamRepository.recuperarPorNombreYLiga("team2", "league2").get();
        Player jugadorRecuperado = team1.getPlayers().getFirst();
        assertEquals(player1.getName(), jugadorRecuperado.getName());
        assertNotNull(jugadorRecuperado.getId());

    }

    @Test
    @Transactional
    void upsertearUnEquipoActualizaLosDatosDeSusJugadores() {
        Player player1 = getJugadorConNombre("player1");
        Team team = new Team("team2", "league2", List.of(player1));
        player1.setTeam(team);
        Team teamRecuperado =teamRepository.guardar(team);

        Player player1Recuperado = teamRecuperado.getPlayers().getFirst();
        player1Recuperado.getPlayerGameData().setRating(9.0);

        Team teamActualizado = teamRepository.upsertTeam(team);
        Player player1Actualizado = teamActualizado.getPlayers().getFirst();;
        assertEquals(9.0, player1Actualizado.getPlayerGameData().getRating());

    }

    @Test
    @Transactional
    void upsertearUnEquipoIntroduceJugadoresNuevosDeSerNecesario() {
        Player player1 = getJugadorConNombre("player1");
        Team team = new Team("team2", "league2", List.of(player1));
        player1.setTeam(team);
        Team teamRecuperado =teamRepository.guardar(team);

        Player player2 = getJugadorConNombre("player2");
        teamRecuperado.addPlayer(player2);

        Team teamActualizado = teamRepository.upsertTeam(team);

        assertTrue(teamActualizado.getPlayers().stream().anyMatch(p -> p.getName().equals("player2")));
        assertTrue(teamActualizado.getPlayers().stream().anyMatch(p -> p.getName().equals("player1")));

    }

}
