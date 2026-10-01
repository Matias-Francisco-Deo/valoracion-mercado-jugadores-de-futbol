package com.overcode.persistence.repository.dao.external.scrapper.whoscored;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scrapper.http.ScraperHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalPlayerWhoScoredScrapperTest {

    @Mock
    private ScraperHttpClient httpClient;

    @InjectMocks
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

    @Test
    @DisplayName("Maneja con seguridad jugador con 0 pases intentados y rating 0")
    void manejaSinErroresJugadorConCeroPasesIntentadosYCeroPartidosConRating() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createZeroPassesAndZeroRatingHtml());

        Optional<Player> resultado = scrapper.getDatosDeJugador(11119L, jugadorDraft);

        assertTrue(resultado.isPresent());
        Player player = resultado.get();

        assertEquals(0, player.getPasses());
        assertEquals(0.0, player.getRating());
    }

    @Test
    @DisplayName("Lanza excepcion cuando el jugador no tiene partidos en ligas Top-5")
    void lanzaExcepcionCuandoJugadorNoRegistraPartidosEnLasCincoLigasPrincipales() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createZeroTop5AppearancesHtml());

        ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
            scrapper.getDatosDeJugador(11119L, jugadorDraft);
        });

        assertTrue(exception.getMessage().contains("El jugador no registra actividad en ninguna de las 5 ligas principales."));
    }

    @Test
    @DisplayName("Lanza excepcion cuando falta el nodo tournaments o es invalido")
    void lanzaExcepcionCuandoElJsonNoContieneElNodoTournamentsOEsInvalido() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createMissingTournamentsNodeHtml());

        ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
            scrapper.getDatosDeJugador(11119L, jugadorDraft);
        });

        assertTrue(exception.getMessage().contains("No se encontró el nodo 'tournaments' en el JSON."));
    }

    @Test
    @DisplayName("Lanza excepcion cuando el HTML no contiene el bloque de JSON args")
    void lanzaExcepcionCuandoElHtmlNoContieneElBloqueJsonEsperado() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createHtmlWithoutArgsBlock());

        assertThrows(ScraperExtractionException.class, () -> {
            scrapper.getDatosDeJugador(11119L, jugadorDraft);
        });
    }

    @Test
    @DisplayName("Lanza excepcion cuando el JSON esta corrupto")
    void lanzaExcepcionCuandoElJsonTieneSintaxisCorrupta() {
        when(httpClient.getHtml(anyString())).thenReturn(WhoScoredTestFixtures.createCorruptedJsonHtml());

        ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
            scrapper.getDatosDeJugador(11119L, jugadorDraft);
        });

        assertTrue(exception.getMessage().contains("Error al parsear el JSON de estadísticas con Jackson."));
    }

    @Test
    @DisplayName("Propaga la excepcion cuando falla el cliente HTTP")
    void propagaExcepcionCuandoElHttpClientFalla() {
        when(httpClient.getHtml(anyString())).thenThrow(new ScraperExtractionException("Error fatal ejecutando Playwright"));

        ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
            scrapper.getDatosDeJugador(11119L, jugadorDraft);
        });

        assertTrue(exception.getMessage().contains("Error fatal ejecutando Playwright"));
    }
}
