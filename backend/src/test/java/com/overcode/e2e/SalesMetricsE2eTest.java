package com.overcode.e2e;

import com.overcode.controller.dto.metrics.MonthlySalesResponseDTO;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.TestUserUtil;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SalesMetricsE2eTest {

    private static final String BASE_PATH = "/api/admin/metrics/sales";
    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String API_KEY_INVALIDA = "clave-invalida-para-tests";

    @LocalServerPort
    private int port;

    @Value("${scraper.api.key}")
    private String apiKey;

    @Autowired
    private TestService testService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private RestClient restClient;
    private User usuarioTest;
    private Player jugadorTest;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();

        usuarioTest = userRepository.guardar(TestUserUtil.getUsuarioDeNombre("AdminMetricsUser"));
        Player player = new Player();
        player.setName("JugadorMetrica");
        player.setCurrentPrice(100.0);
        jugadorTest = playerRepository.guardar(player);
    }

    @AfterEach
    void tearDown() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void debeRetornarCantidadVentasCuandoSeEspecificaAnioYMes() {
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 12, 0), TransactionType.SELL, 2, 100.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 20, 15, 30), TransactionType.SELL, 1, 150.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 10, 8, 0), TransactionType.BUY, 3, 100.0); // compra ignorada
        guardarTransaccion(LocalDateTime.of(2026, 10, 15, 12, 0), TransactionType.SELL, 1, 100.0); // otro mes ignorado

        ResponseEntity<MonthlySalesResponseDTO> respuesta = restClient.get()
                .uri(BASE_PATH + "?year=2026&month=11")
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(MonthlySalesResponseDTO.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2026, respuesta.getBody().year());
        assertEquals(11, respuesta.getBody().month());
        assertEquals(2L, respuesta.getBody().totalSales());
    }

    @Test
    void debeRetornarCeroCuandoNoHayVentasEnElMes() {
        ResponseEntity<MonthlySalesResponseDTO> respuesta = restClient.get()
                .uri(BASE_PATH + "?year=2025&month=1")
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(MonthlySalesResponseDTO.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2025, respuesta.getBody().year());
        assertEquals(1, respuesta.getBody().month());
        assertEquals(0L, respuesta.getBody().totalSales());
    }

    @Test
    void debeRetornarVentasDelMesActualCuandoNoSeEnvianParametros() {
        guardarTransaccion(LocalDateTime.now(), TransactionType.SELL, 1, 200.0);

        ResponseEntity<MonthlySalesResponseDTO> respuesta = restClient.get()
                .uri(BASE_PATH)
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(MonthlySalesResponseDTO.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        LocalDate hoy = LocalDate.now();
        assertEquals(hoy.getYear(), respuesta.getBody().year());
        assertEquals(hoy.getMonthValue(), respuesta.getBody().month());
        assertTrue(respuesta.getBody().totalSales() >= 1L);
    }

    @Test
    void debeRetornarUnauthorizedCuandoFaltaApiKey() {
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.get()
                        .uri(BASE_PATH)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRetornarUnauthorizedCuandoApiKeyEsInvalida() {
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.get()
                        .uri(BASE_PATH)
                        .header(HEADER_API_KEY, API_KEY_INVALIDA)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRetornarBadRequestCuandoMesEsInvalido() {
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.get()
                        .uri(BASE_PATH + "?year=2026&month=13")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRetornarBadRequestCuandoAnioEsInvalido() {
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.get()
                        .uri(BASE_PATH + "?year=0&month=5")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    private void guardarTransaccion(LocalDateTime fecha, TransactionType tipo, Integer cantidad, Double precioUnitario) {
        TransaccionFinanciera tx = new TransaccionFinanciera(null, usuarioTest, jugadorTest, fecha, tipo, cantidad, precioUnitario);
        transactionRepository.guardar(tx);
    }
}
