package com.overcode.persistence.repository;

import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.model.player.TeamUpsertResult;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.testUtils.TestService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static junit.framework.TestCase.*;


@SpringBootTest
class TeamRepositoryTest {

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
    @Transactional
    void recuperarPorIdTraeAlEquipo() {
        Team team = getTeamConJugadores(List.of());
        Team teamPersistido = teamRepository.guardar(team);

        assertTrue(teamRepository.recuperar(teamPersistido.getId()).isPresent());

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
        player1.setExternalId(1L);
        Team team = new Team("team2", "league2", List.of(player1));
        player1.setTeam(team);
        Team teamRecuperado =teamRepository.guardar(team);

        Player player1Recuperado = teamRecuperado.getPlayers().getFirst();
        player1Recuperado.getPlayerGameData().setRating(9.0);

        Team teamActualizado = teamRepository.upsertTeam(teamRecuperado).team();
        Player player1Actualizado = teamActualizado.getPlayers().getFirst();
        assertEquals(9.0, player1Actualizado.getPlayerGameData().getRating());

    }

    @Test
    @Transactional
    void upsertearUnEquipoIntroduceJugadoresNuevos() {
        Player player1 = getJugadorConNombre("player1");
        player1.setExternalId(1L);
        Team team = new Team("team2", "league2", new ArrayList<>(List.of(player1)));
        player1.setTeam(team);
        Team teamRecuperado =teamRepository.guardar(team);

        entityManager.flush();

        Player player2 = getJugadorConNombre("player2");
        teamRecuperado.addPlayer(player2);

        TeamUpsertResult result = teamRepository.upsertTeam(teamRecuperado);
        Team teamActualizado = result.team();

        assertTrue(teamActualizado.getPlayers().stream().anyMatch(p -> p.getName().equals("player2")));
        assertTrue(teamActualizado.getPlayers().stream().anyMatch(p -> p.getName().equals("player1")));
        assertEquals(1, result.newPlayers().size());
        assertEquals("player2", result.newPlayers().getFirst().getName());
    }

    @Test
    @Transactional
    void upsertearUnEquipoExistenteActualizaJugadorSinExternalIdSinDuplicarlo() {
        Player existingPlayer = getJugadorConNombre("Harry Wilson");
        existingPlayer.setExternalId(null);
        existingPlayer.getPlayerGameData().setRating(6.5);
        Team initialTeam = new Team("Fulham", "Premier League", new ArrayList<>(List.of(existingPlayer)));
        existingPlayer.setTeam(initialTeam);
        teamRepository.guardar(initialTeam);

        entityManager.flush();
        entityManager.clear();

        Player incomingPlayer = getJugadorConNombre("Harry Wilson");
        incomingPlayer.setExternalId(12345L);
        incomingPlayer.getPlayerGameData().setRating(8.2);
        Team incomingTeam = new Team("Fulham", "Premier League", new ArrayList<>(List.of(incomingPlayer)));
        incomingPlayer.setTeam(incomingTeam);

        TeamUpsertResult result = teamRepository.upsertTeam(incomingTeam);
        entityManager.flush();

        Team updatedTeam = result.team();
        assertEquals(1, updatedTeam.getPlayers().size());
        Player persisted = updatedTeam.getPlayers().getFirst();
        assertEquals("Harry Wilson", persisted.getName());
        assertEquals(Long.valueOf(12345L), persisted.getExternalId());
        assertEquals(8.2, persisted.getPlayerGameData().getRating());
        assertTrue(result.newPlayers().isEmpty());
    }

    @Test
    @Transactional
    void upsertearUnEquipoExistenteReasignaJugadorQueYaExistiaEnOtroEquipo() {
        Player existingPlayer = getJugadorConNombre("Harry Wilson");
        existingPlayer.setExternalId(12345L);
        existingPlayer.getPlayerGameData().setRating(6.8);
        Team liverpool = new Team("Liverpool", "Premier League", new ArrayList<>(List.of(existingPlayer)));
        existingPlayer.setTeam(liverpool);
        teamRepository.guardar(liverpool);

        Team fulham = new Team("Fulham", "Premier League", new ArrayList<>());
        teamRepository.guardar(fulham);

        entityManager.flush();
        entityManager.clear();

        Player transferredPlayer = getJugadorConNombre("Harry Wilson");
        transferredPlayer.setExternalId(12345L);
        transferredPlayer.getPlayerGameData().setRating(8.0);
        Team incomingFulham = new Team("Fulham", "Premier League", new ArrayList<>(List.of(transferredPlayer)));
        transferredPlayer.setTeam(incomingFulham);

        TeamUpsertResult result = teamRepository.upsertTeam(incomingFulham);
        entityManager.flush();

        Team updatedFulham = teamRepository.recuperarPorNombreYLiga("Fulham", "Premier League").orElseThrow();
        assertEquals(1, updatedFulham.getPlayers().size());
        Player persisted = updatedFulham.getPlayers().getFirst();
        assertEquals(Long.valueOf(12345L), persisted.getExternalId());
        assertEquals(8.0, persisted.getPlayerGameData().getRating());
        assertTrue(result.newPlayers().isEmpty());
    }

    @Test
    @Transactional
    void upsertearUnEquipoNuevoNoDuplicaJugadorQueYaExistiaEnOtroEquipo() {
        Player existingPlayer = getJugadorConNombre("Harry Wilson");
        existingPlayer.setExternalId(12345L);
        existingPlayer.getPlayerGameData().setRating(6.8);
        Team liverpool = new Team("Liverpool", "Premier League", new ArrayList<>(List.of(existingPlayer)));
        existingPlayer.setTeam(liverpool);
        teamRepository.guardar(liverpool);

        entityManager.flush();
        entityManager.clear();

        Player transferredPlayer = getJugadorConNombre("Harry Wilson");
        transferredPlayer.setExternalId(12345L);
        transferredPlayer.getPlayerGameData().setRating(8.5);
        Team newFulham = new Team("Fulham", "Premier League", new ArrayList<>(List.of(transferredPlayer)));
        transferredPlayer.setTeam(newFulham);

        TeamUpsertResult result = teamRepository.upsertTeam(newFulham);
        entityManager.flush();

        Team savedFulham = teamRepository.recuperarPorNombreYLiga("Fulham", "Premier League").orElseThrow();
        assertEquals(1, savedFulham.getPlayers().size());
        assertEquals(8.5, savedFulham.getPlayers().getFirst().getPlayerGameData().getRating());
        assertTrue(result.newPlayers().isEmpty());
    }

    @Test
    @Transactional
    void upsertearEquiposPermiteJugadoresDistintosConMismoNombreYDistintoExternalId() {
        Player firstPlayer = getJugadorConNombre("Harry Wilson");
        firstPlayer.setExternalId(100L);
        Team team1 = new Team("Fulham", "Premier League", new ArrayList<>(List.of(firstPlayer)));
        firstPlayer.setTeam(team1);
        teamRepository.upsertTeam(team1);

        entityManager.flush();
        entityManager.clear();

        Player secondPlayer = getJugadorConNombre("Harry Wilson");
        secondPlayer.setExternalId(200L);
        Team team2 = new Team("Otro Club", "LaLiga", new ArrayList<>(List.of(secondPlayer)));
        secondPlayer.setTeam(team2);

        TeamUpsertResult result = teamRepository.upsertTeam(team2);
        entityManager.flush();

        assertEquals(1, result.team().getPlayers().size());
        assertEquals(Long.valueOf(200L), result.team().getPlayers().getFirst().getExternalId());
        assertEquals(1, result.newPlayers().size());
    }

    @Test
    @Transactional
    void upsertearUnEquipoDeduplicaJugadoresRepetidosEnLaMismaLista() {
        Player firstCopy = getJugadorConNombre("Adama Traore");
        firstCopy.setExternalId(555L);
        Player secondCopy = getJugadorConNombre("Adama Traore");
        secondCopy.setExternalId(555L);

        Team team = new Team("Fulham", "Premier League", new ArrayList<>(List.of(firstCopy, secondCopy)));
        firstCopy.setTeam(team);
        secondCopy.setTeam(team);

        TeamUpsertResult result = teamRepository.upsertTeam(team);
        entityManager.flush();

        assertEquals(1, result.team().getPlayers().size());
        assertEquals(1, result.newPlayers().size());
    }

}
