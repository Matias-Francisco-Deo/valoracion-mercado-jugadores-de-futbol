package com.overcode.persistence.repository.dao.external.scrapper.whoscored;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scrapper.http.ScraperHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExternalPlayerWhoScoredScrapperTest {

    @MockitoBean
    private ScraperHttpClient httpClient;

    @Autowired
    private ExternalPlayerWhoScoredScrapper scrapper;

    private final PlayerDraftDTO jugadorDraft = new PlayerDraftDTO("Kylian Mbappé", "Real Madrid CF", "La Liga");

    @Test
    @DisplayName("Extrae métricas acumuladas correctamente filtrando torneos no pertenecientes a las 5 grandes ligas")
    void extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createValidMultiLeagueHtml());

        Optional<Player> resultado = scrapper.getDatosDeJugador(11119L, jugadorDraft);

        assertTrue(resultado.isPresent(), "El jugador debería haberse extraído correctamente");
        Player player = resultado.get();

        // Verificamos metadatos del draft y externalId
        assertEquals(11119L, player.getExternalId());
        assertEquals("Kylian Mbappé", player.getName());
        assertEquals("Real Madrid CF", player.getClubName());
        assertEquals("La Liga", player.getLeague());
        assertEquals(1, player.getCurrentPrice());

        // Verificamos acumulación exclusiva de ligas Top 5 (Torneo 4 y Torneo 2, ignorando Torneo 999)
        assertEquals(12, player.getGoals());
        assertEquals(5, player.getAssists());
        assertEquals(24, player.getShotsOnTarget());
        assertEquals(8, player.getInterceptions());
        assertEquals(11, player.getTackles());
        assertEquals(16, player.getKeyPasses());
        assertEquals(20, player.getSuccessfulDribbles());
    }

    @Test
    @DisplayName("Calcula el promedio ponderado del rating según partidos jugados y el porcentaje de pases precisos")
    void calculaRatingPonderadoYPorcentajeDePasesExitososCorrectamente() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createValidMultiLeagueHtml());

        Optional<Player> resultado = scrapper.getDatosDeJugador(11119L, jugadorDraft);

        assertTrue(resultado.isPresent());
        Player player = resultado.get();

        // Pases: 425 / 500 = 85%
        assertEquals(85, player.getPasses());

        // Rating ponderado: (7.80*12 + 7.20*4) / 16 = 122.4 / 16 = 7.65
        assertEquals(7.65, player.getRating());
    }
}
