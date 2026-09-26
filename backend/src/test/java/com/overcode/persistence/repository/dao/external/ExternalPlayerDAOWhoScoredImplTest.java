package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.ExternalPlayerWhoScoredScrapper;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.WhoScoredIdResolver;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public class ExternalPlayerDAOWhoScoredImplTest {

    @Autowired
    private ExternalPlayerDAOWhoScoredImpl externalPlayerDAOWhoScoredImpl;

    private ExternalPlayerDAOWhoScoredImpl externalPlayerDAOWhoScoredImplMock;
    private WhoScoredIdResolver whoScoredIdResolverMock;
    private ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapperMock;

    @Autowired
    private PlayerDAOJPA playerDAOJPA;

    private final PlayerDraftDTO JUGADOR_DRAFT_1 = new PlayerDraftDTO("Kylian Mbappé", "Real Madrid CF", "La Liga");

    @BeforeEach
    void setUp() {
        whoScoredIdResolverMock = mock(WhoScoredIdResolver.class);
        externalPlayerWhoScoredScrapperMock = mock(ExternalPlayerWhoScoredScrapper.class);
        externalPlayerDAOWhoScoredImplMock = new ExternalPlayerDAOWhoScoredImpl(whoScoredIdResolverMock, externalPlayerWhoScoredScrapperMock, playerDAOJPA);
    }

    @Test
    void encuentraJugadorConDatosMock() {
        Player mockPlayer = new Player();
        mockPlayer.setName("Kylian Mbappé");
        mockPlayer.setClubName("Real Madrid CF");
        mockPlayer.setGoals(15);
        mockPlayer.setRating(8.5);

        when(whoScoredIdResolverMock.resolvePlayerId("Kylian Mbappé")).thenReturn(11119L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(11119L, JUGADOR_DRAFT_1)).thenReturn(Optional.of(mockPlayer));

        Optional<Player> playerOpt = externalPlayerDAOWhoScoredImplMock.getDatosDeJugador(JUGADOR_DRAFT_1);

        assertTrue(playerOpt.isPresent());
        Player player = playerOpt.get();
        assertEquals("Kylian Mbappé", player.getName());
        assertEquals("Real Madrid CF", player.getClubName());
        assertEquals(15, player.getGoals());
        assertEquals(8.5, player.getRating());
    }

    @Test
    void noEncuentraJugadorInexistenteYDevuelveVacioMock() {
        PlayerDraftDTO JUGADOR_FANTASMA = new PlayerDraftDTO("Jugador Fantasma", "Club Fantasma", "Liga Fantasma");

        when(whoScoredIdResolverMock.resolvePlayerId(anyString())).thenReturn(null);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(null, JUGADOR_FANTASMA)).thenReturn(Optional.empty());

        Optional<Player> playerOpt = externalPlayerDAOWhoScoredImplMock.getDatosDeJugador(JUGADOR_FANTASMA);

        assertTrue(playerOpt.isEmpty());
    }

    @Test
    void encuentraVariosJugadoresMock() {
        PlayerDraftDTO JUGADOR_DRAFT_2 = new PlayerDraftDTO("Vinícius Júnior", "Real Madrid CF", "La Liga");
        
        Player mockPlayer1 = new Player();
        mockPlayer1.setName("Kylian Mbappé");
        
        Player mockPlayer2 = new Player();
        mockPlayer2.setName("Vinícius Júnior");

        when(whoScoredIdResolverMock.resolvePlayerId("Kylian Mbappé")).thenReturn(11119L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(11119L, JUGADOR_DRAFT_1)).thenReturn(Optional.of(mockPlayer1));

        when(whoScoredIdResolverMock.resolvePlayerId("Vinícius Júnior")).thenReturn(22222L);
        when(externalPlayerWhoScoredScrapperMock.getDatosDeJugador(22222L, JUGADOR_DRAFT_2)).thenReturn(Optional.of(mockPlayer2));

        List<Player> jugadores = externalPlayerDAOWhoScoredImplMock.getDatosJugadores(List.of(JUGADOR_DRAFT_1, JUGADOR_DRAFT_2)).get();

        assertFalse(jugadores.isEmpty());
        assertEquals(2, jugadores.size());
        assertEquals("Kylian Mbappé", jugadores.get(0).getName());
        assertEquals("Vinícius Júnior", jugadores.get(1).getName());
    }

    @Disabled("Use manually since it can fail if the scraper blocks or takes too long")
    @Test
    void encuentraJugadorConDatosReal(){
        Optional<Player> playerOpt = externalPlayerDAOWhoScoredImpl.getDatosDeJugador(JUGADOR_DRAFT_1);

        assertTrue(playerOpt.isPresent());
        Player player = playerOpt.get();
        assertNotNull(player.getName());
        assertNotNull(player.getClubName());
        assertNotNull(player.getGoals());
        assertNotNull(player.getAssists());
        assertNotNull(player.getRating());
        assertNotNull(player.getInterceptions());
        assertNotNull(player.getShotsOnTarget());
        assertNotNull(player.getSuccessfulDribbles());
        assertNotNull(player.getTackles());
        assertNotNull(player.getKeyPasses());
    }
}
