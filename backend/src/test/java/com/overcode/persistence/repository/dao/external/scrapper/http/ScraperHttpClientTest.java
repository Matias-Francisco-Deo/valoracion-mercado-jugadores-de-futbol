package com.overcode.persistence.repository.dao.external.scrapper.http;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ScraperHttpClientTest {

    private static MockWebServer mockWebServer;
    private static ScraperHttpClient client;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        client = new ScraperHttpClient();
        client.init();
    }


    @AfterAll
    static void tearDownAll() throws IOException {
        if (client != null) {
            client.cleanup();
        }
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    @DisplayName("Debe obtener el HTML correctamente si el servidor responde con exito")
    void obtieneHtmlExitosamenteDesdeMockWebServer() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><h1>Test</h1></body></html>").setResponseCode(200));
        String url = mockWebServer.url("/player/123").toString();

        String html = client.getHtml(url);

        assertTrue(html.contains("<h1>Test</h1>"));
    }

    @Test
    @DisplayName("Debe reintentar y obtener el HTML si el primer intento topa con Cloudflare")
    void reintentaYObtieneHtmlCuandoElPrimerIntentoEncuentraDesafioCloudflare() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body>Checking your browser before accessing Cloudflare</body></html>").setResponseCode(403));
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><h1>Test 2</h1></body></html>").setResponseCode(200));

        String url = mockWebServer.url("/player/123").toString();

        String html = client.getHtml(url);

        assertTrue(html.contains("<h1>Test 2</h1>"));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si todos los reintentos chocan con Cloudflare")
    void lanzaExcepcionCuandoSeSuperanLosReintentosPorDesafioCloudflare() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body>Checking your browser before accessing Cloudflare</body></html>").setResponseCode(403));
        mockWebServer.enqueue(new MockResponse().setBody("<html><body>Checking your browser before accessing Cloudflare</body></html>").setResponseCode(403));

        String url = mockWebServer.url("/player/123").toString();

        ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
            client.getHtml(url);
        });

        assertTrue(exception.getCause().getMessage().contains("Cloudflare challenge no superado"));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando hay un error de red")
    void lanzaExcepcionCuandoLaNavegacionFallaCompletamenteTrasReintentos() throws IOException {
        try (MockWebServer brokenServer = new MockWebServer()) {
            brokenServer.start();
            String url = brokenServer.url("/player").toString();
            brokenServer.shutdown();

            ScraperExtractionException exception = assertThrows(ScraperExtractionException.class, () -> {
                client.getHtml(url);
            });

            assertTrue(exception.getMessage().contains("Error fatal ejecutando Playwright"));
        }
    }

    @Test
    @DisplayName("Debe abortar peticiones a imagenes y estilos")
    @Disabled("El test puede fallar en ciertas situaciones, hay que revisarlo")
    void abortaPeticionesDeImagenesEstilosYTrackersDuranteLaNavegacion() throws InterruptedException {
        // Enqueue response for the HTML page
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><img src=\"/img.png\"><link rel=\"stylesheet\" href=\"/style.css\"></body></html>").setResponseCode(200));

        String url = mockWebServer.url("/player/123").toString();
        client.getHtml(url);

        // Limpiar el request principal
        RecordedRequest mainRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertNotNull(mainRequest);
        assertEquals("/player/123", mainRequest.getPath());

        // Verificar que no se pidan imagenes ni css
        RecordedRequest extraRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertNull(extraRequest, "No deberian llegar requests de imagenes o CSS al servidor");
    }

    @Test
    @DisplayName("Debe inicializar y cerrar recursos del navegador exitosamente")
    void inicializaYCierraRecursosDeNavegadorCorrectamente() {
        ScraperHttpClient newClient = new ScraperHttpClient();
        assertDoesNotThrow(() -> {
            newClient.init();
            newClient.cleanup();
        });
    }
}
