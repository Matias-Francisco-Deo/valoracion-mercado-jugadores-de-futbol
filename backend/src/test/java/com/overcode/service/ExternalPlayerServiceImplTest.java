package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.impl.ExternalPlayerServiceImpl;
import com.overcode.service.interfaces.PlayerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExternalPlayerServiceImplTest {

    @Autowired
    private ExternalPlayerServiceImpl externalPlayerServiceImpl;

    @Autowired
    private PlayerService playerService;

    @MockitoBean
    private ExternalPlayerRepository externalPlayerRepository;

    @MockitoBean
    private TeamRepository teamRepositoryMock;

    @InjectMocks
    private ExternalPlayerServiceImpl mockExternalPlayerServiceImpl;

    @Test
    void encuentraJugadoresConDatosMock(){

        PlayerDraftDTO player = new PlayerDraftDTO("Jorgelin");
        TeamDraftDTO teamDTO = new TeamDraftDTO("Club1", "Liga1", List.of(player));

        when(externalPlayerRepository.listarEquiposDeJugadores(anyInt()))
                .thenReturn(Optional.of(List.of(teamDTO)));

        Team team = getTeamConJugadores(List.of(getJugadorConNombre("Jorgelín")));

        when(externalPlayerRepository.getDatosDeEquipos(List.of(teamDTO)))
                .thenReturn(Optional.of(List.of(team)));

        when(teamRepositoryMock.upsertTeam(team)).thenReturn(team);

        Optional<List<Player>> optionalPlayers = mockExternalPlayerServiceImpl.actualizarJugadores(1);

        assertTrue(optionalPlayers.isPresent());
        List<Player> players = optionalPlayers.get();
        assertFalse(players.isEmpty());

        players.forEach((p -> {
            assertNotNull(p.getTokens());
            assertNotNull(p.getName());
            assertNotNull(p.getTeam());
            assertNotNull(p.getPlayerGameData());
            assertNotNull(p.getPlayerGameData().getRating());
            assertNotNull(p.getPlayerGameData().getAssists());
        }
        ));
    }

    @Test
    @Disabled("Use automatically to generate players up to the limit set in the method")
    void encuentraJugadoresConDatos(){
        Optional<List<Player>> optionalPlayers = externalPlayerServiceImpl.actualizarJugadores(1);

        assertTrue(optionalPlayers.isPresent());
        List<Player> players = optionalPlayers.get();
        assertFalse(players.isEmpty());

        players.forEach((player -> {
            assertNotNull(player.getId());
            assertNotNull(player.getTokens());
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertNotNull(player.getPlayerGameData());
            assertNotNull(player.getPlayerGameData().getRating());
            assertNotNull(player.getPlayerGameData().getAssists());
            assertNotNull(player.getPlayerGameData().getPosition());
        }
        ));
    }

    @Test
    @Disabled("Use automatically to generate players up to the limit set in the method")
    void encuentraJugadoresConDatosAsync(){
        CompletableFuture<Void> future = externalPlayerServiceImpl.actualizarJugadoresAsync();

        future.join();

        List<Player> players = playerService.recuperarTodos();

        assertFalse(players.isEmpty());

        players.forEach((player -> {
            assertNotNull(player.getId());
            assertNotNull(player.getTokens());
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertNotNull(player.getPlayerGameData());
        }
        ));
    }

    @Test
    void encuentraJugadoresConDatosAsyncMock(){

        PlayerDraftDTO player = new PlayerDraftDTO("Jorgelin");
        TeamDraftDTO teamDTO = new TeamDraftDTO("Club1", "Liga1", List.of(player));

        when(externalPlayerRepository.listarEquiposDeJugadores(anyInt()))
                .thenReturn(Optional.of(List.of(teamDTO)));

        Team team = getTeamConJugadores(List.of(getJugadorConNombre("Jorgelín")));

        when(externalPlayerRepository.getDatosDeEquipos(List.of(teamDTO)))
                .thenReturn(Optional.of(List.of(team)));

        when(teamRepositoryMock.upsertTeam(team)).thenReturn(team);

        CompletableFuture<Void> future = mockExternalPlayerServiceImpl.actualizarJugadoresAsync();

        future.join();

        assertTrue(future.isDone());
    }

}
