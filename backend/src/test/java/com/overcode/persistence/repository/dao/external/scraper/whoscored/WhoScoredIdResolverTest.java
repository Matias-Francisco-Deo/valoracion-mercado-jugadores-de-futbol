package com.overcode.persistence.repository.dao.external.scraper.whoscored;

import com.overcode.persistence.repository.dao.external.scraper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scraper.http.ScraperHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhoScoredIdResolverTest {

    @Mock
    private ScraperHttpClient httpClient;

    @InjectMocks
    private WhoScoredIdResolver resolver;

    @Test
    @DisplayName("Resuelve el ID exitosamente en la búsqueda directa inicial")
    void resuelveIdExitosamenteEnBusquedaDirecta() {
        String expectedUrl = "https://www.whoscored.com/Search/?t=Kylian+Mbapp%C3%A9";
        String html = createSearchHtml(playerLink(300713L, "kylian-mbappe"));

        when(httpClient.getHtml(expectedUrl)).thenReturn(html);

        Long playerId = resolver.resolvePlayerId("Kylian Mbappé");

        assertEquals(300713L, playerId);
        verify(httpClient, times(1)).getHtml(expectedUrl);
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    @DisplayName("Normaliza caracteres diacríticos (tildes) y diferencias de mayúsculas/minúsculas")
    void normalizaCaracteresDiacriticosYMayusculasCorrectamente() {
        String expectedUrl = "https://www.whoscored.com/Search/?t=%C3%89der+Milit%C3%A3o";
        // HTML con mayúsculas y acentos en el contenido, slug normalizado
        String html = "<html><body><a href=\"/players/345678/show/eder-militao\">ÉDER MILITÃO</a></body></html>";

        when(httpClient.getHtml(expectedUrl)).thenReturn(html);

        Long playerId = resolver.resolvePlayerId("Éder Militão");

        assertEquals(345678L, playerId);
    }

    @Test
    @DisplayName("Ejecuta fallback por apellido cuando la búsqueda directa no arroja coincidencias")
    void ejecutaFallbackPorApellidoCuandoBusquedaDirectaFalla() {
        String directUrl = "https://www.whoscored.com/Search/?t=Andriy+Lunin";
        String fallbackUrl = "https://www.whoscored.com/Search/?t=Lunin";

        String directHtml = createSearchHtml("<div>No results found</div>");
        // En WhoScored figura como "andrii-lunin", por lo que "lunin" coincidirá en el fallback
        String fallbackHtml = createSearchHtml(playerLink(363849L, "andrii-lunin"));

        when(httpClient.getHtml(directUrl)).thenReturn(directHtml);
        when(httpClient.getHtml(fallbackUrl)).thenReturn(fallbackHtml);

        Long playerId = resolver.resolvePlayerId("Andriy Lunin");

        assertEquals(363849L, playerId);

        InOrder inOrder = inOrder(httpClient);
        inOrder.verify(httpClient).getHtml(directUrl);
        inOrder.verify(httpClient).getHtml(fallbackUrl);
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    @DisplayName("Selecciona el jugador con mayor puntaje cuando el HTML contiene múltiples candidatos")
    void seleccionaJugadorConMayorPuntajeCuandoHayMultiplesCandidatos() {
        String searchUrl = "https://www.whoscored.com/Search/?t=Gabriel+Martinelli";
        // Candidato 1: match solo "gabriel" (score 1)
        // Candidato 2: match "gabriel" y "martinelli" (score 2)
        // Candidato 3: no matchea palabras > 2 caracteres (score 0)
        String html = createSearchHtml(
                playerLink(101L, "gabriel"),
                playerLink(102L, "gabriel-martinelli"),
                playerLink(103L, "martin")
        );

        when(httpClient.getHtml(searchUrl)).thenReturn(html);

        Long playerId = resolver.resolvePlayerId("Gabriel Martinelli");

        assertEquals(102L, playerId);
    }

    @Test
    @DisplayName("Ignora palabras cortas de dos caracteres o menos al calcular el score de coincidencia")
    void ignoraPalabrasDeDosCaracteresOMenosEnElScoring() {
        String searchUrl = "https://www.whoscored.com/Search/?t=Kevin+De+Bruyne";
        // "De" tiene longitud 2, no debe sumar score.
        // Candidato 1 tiene "de", score esperado: 0
        // Candidato 2 tiene "kevin" y "bruyne", score esperado: 2
        String html = createSearchHtml(
                playerLink(201L, "de-jong"),
                playerLink(202L, "kevin-de-bruyne")
        );

        when(httpClient.getHtml(searchUrl)).thenReturn(html);

        Long playerId = resolver.resolvePlayerId("Kevin De Bruyne");

        assertEquals(202L, playerId);
    }

    @Test
    @DisplayName("Lanza ScraperExtractionException cuando ni la búsqueda directa ni el fallback encuentran al jugador")
    void lanzaExcepcionCuandoNoEncuentraCoincidenciasNiEnDirectaNiEnFallback() {
        String directUrl = "https://www.whoscored.com/Search/?t=Jugador+Inexistente";
        String fallbackUrl = "https://www.whoscored.com/Search/?t=Inexistente";

        String emptyHtml = createSearchHtml("<div>Sin resultados</div>");

        when(httpClient.getHtml(directUrl)).thenReturn(emptyHtml);
        when(httpClient.getHtml(fallbackUrl)).thenReturn(emptyHtml);

        ScraperExtractionException exception = assertThrows(
                ScraperExtractionException.class,
                () -> resolver.resolvePlayerId("Jugador Inexistente")
        );

        assertTrue(exception.getMessage().contains("No se encontró el ID del jugador en el HTML para: Jugador Inexistente"));
    }

    @Test
    @DisplayName("Lanza ScraperExtractionException si hay links de jugadores pero ninguno tiene score mayor a cero")
    void lanzaExcepcionCuandoHayLinksDeJugadoresPeroScoreEsCero() {
        String directUrl = "https://www.whoscored.com/Search/?t=Lionel+Messi";
        String fallbackUrl = "https://www.whoscored.com/Search/?t=Messi";

        // Los links encontrados corresponden a jugadores completamente distintos
        String unmatchedHtml = createSearchHtml(playerLink(999L, "cristiano-ronaldo"));

        when(httpClient.getHtml(directUrl)).thenReturn(unmatchedHtml);
        when(httpClient.getHtml(fallbackUrl)).thenReturn(unmatchedHtml);

        assertThrows(
                ScraperExtractionException.class,
                () -> resolver.resolvePlayerId("Lionel Messi")
        );
    }

    @Test
    @DisplayName("Propaga la excepción cuando el cliente HTTP falla durante la búsqueda directa")
    void propagaExcepcionCuandoHttpClientFallaEnBusquedaInicial() {
        when(httpClient.getHtml(anyString())).thenThrow(new ScraperExtractionException("Error de conexión al sitio"));

        ScraperExtractionException exception = assertThrows(
                ScraperExtractionException.class,
                () -> resolver.resolvePlayerId("Cualquier Jugador")
        );

        assertTrue(exception.getMessage().contains("Error de conexión al sitio"));
        // Comprueba que no intentó ejecutar el fallback si la llamada HTTP falló antes del bloque try/catch
        verify(httpClient, times(1)).getHtml(anyString());
    }

    private static String createSearchHtml(String... elements) {
        StringBuilder sb = new StringBuilder("<html><body>");
        for (String element : elements) {
            sb.append(element);
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private static String playerLink(Long id, String slug) {
        return "<a href=\"/players/" + id + "/show/" + slug + "\">Ver Perfil</a>";
    }
}
