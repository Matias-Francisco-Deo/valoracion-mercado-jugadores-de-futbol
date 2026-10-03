package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.ExternalPlayerWhoScoredScrapper;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.WhoScoredIdResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExternalPlayerDAOWhoScoredImplTest {

    @Autowired
    private ExternalPlayerDAOWhoScoredImpl externalPlayerDAOWhoScoredImpl;

    private ExternalPlayerDAOWhoScoredImpl externalPlayerDAOWhoScoredImplMock;
    private WhoScoredIdResolver whoScoredIdResolverMock;
    private ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapperMock;
    private final PlayerDraftDTO JUGADOR_DRAFT_1 = new PlayerDraftDTO("Kylian Mbappé");
    private final TeamDraftDTO TEAM_DRAFT_1 = new TeamDraftDTO("Real Madrid CF", "La Liga", List.of(JUGADOR_DRAFT_1));
    private final List<TeamDraftDTO> TEAMS_DRAFT = List.of(TEAM_DRAFT_1);

    @BeforeEach
    void setUp() {
        whoScoredIdResolverMock = mock(WhoScoredIdResolver.class);
        externalPlayerWhoScoredScrapperMock = mock(ExternalPlayerWhoScoredScrapper.class);
        externalPlayerDAOWhoScoredImplMock = new ExternalPlayerDAOWhoScoredImpl(whoScoredIdResolverMock, externalPlayerWhoScoredScrapperMock);
    }

    @Test
    void encuentraJugadorConDatosMock() {
        Player mockPlayer = new Player();
        mockPlayer.setName("Kylian Mbappé");

        when(whoScoredIdResolverMock.resolvePlayerId("Kylian Mbappé")).thenReturn(11119L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(11119L, JUGADOR_DRAFT_1)).thenReturn(Optional.of(mockPlayer));

        Optional<List<Team>> teamOptional = externalPlayerDAOWhoScoredImplMock.getDatosDeEquipos(TEAMS_DRAFT);

        assertTrue(teamOptional.isPresent());
        Player player = teamOptional.get().getFirst().getPlayers().getFirst();
        assertEquals("Kylian Mbappé", player.getName());
    }

    @Test
    void noEncuentraJugadorInexistenteYDevuelveVacioMock() {
        PlayerDraftDTO jugadorFantasma = new PlayerDraftDTO("JugadorInexistente");
        TeamDraftDTO equipoConJugadorFantasma = new TeamDraftDTO("Real Madrid CF", "La Liga", List.of(jugadorFantasma));

        when(whoScoredIdResolverMock.resolvePlayerId(anyString())).thenReturn(null);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(null, jugadorFantasma)).thenReturn(Optional.empty());

        Optional<List<Team>> optionalTeams = externalPlayerDAOWhoScoredImplMock.getDatosDeEquipos(List.of(equipoConJugadorFantasma));

        assertTrue(optionalTeams.isPresent());
        assertTrue(optionalTeams.get().getFirst().getPlayers().isEmpty());
    }

    @Test
    void encuentraVariosJugadoresMock() {
        PlayerDraftDTO jugador2 = new PlayerDraftDTO("Vinícius Júnior");

        Player mockPlayer1 = new Player();
        mockPlayer1.setName("Kylian Mbappé");
        
        Player mockPlayer2 = new Player();
        mockPlayer2.setName("Vinícius Júnior");

        when(whoScoredIdResolverMock.resolvePlayerId("Kylian Mbappé")).thenReturn(11119L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(11119L, JUGADOR_DRAFT_1)).thenReturn(Optional.of(mockPlayer1));

        when(whoScoredIdResolverMock.resolvePlayerId("Vinícius Júnior")).thenReturn(22222L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(22222L, jugador2)).thenReturn(Optional.of(mockPlayer2));

        List<Player> jugadores = externalPlayerDAOWhoScoredImplMock.getDatosJugadores(List.of(JUGADOR_DRAFT_1, jugador2)).get();

        assertFalse(jugadores.isEmpty());
        assertEquals(2, jugadores.size());
        assertEquals("Kylian Mbappé", jugadores.get(0).getName());
        assertEquals("Vinícius Júnior", jugadores.get(1).getName());
    }

}
