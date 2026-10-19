package com.overcode.e2e;

import com.overcode.controller.dto.cotizacion.ActualizarFactorEscalaRequestDTO;
import com.overcode.controller.dto.cotizacion.EstrategiaCotizacionResponseDTO;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.service.impl.ConfiguracionCotizacionesServiceImpl;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2MapAJPADTO;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EstrategiaCotizacionE2eTest {

    private static final String BASE_PATH = "/api/admin/quotes";
    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String API_KEY_INVALIDA = "clave-invalida-para-tests";

    private static final EstrategiaCotizacionSiempre2 ESTRATEGIA_1 =
            new EstrategiaCotizacionSiempre2(0.0, 1.0);

    private static final EstrategiaCotizacionSiempre2 ESTRATEGIA_2 =
            new EstrategiaCotizacionSiempre2(10.0, 4.0);

    @LocalServerPort
    private int port;

    @Value("${scraper.api.key}")
    private String apiKey;

    @Autowired
    private TestService testService;

    @Autowired
    private EstrategiaCotizacionService estrategiaCotizacionService;

    @Autowired
    private PlayerService playerService;

    private RestClient restClient;

    @Autowired
    private ConfiguracionCotizacionesServiceImpl configuracionCotizacionesServiceImpl;

    @Autowired
    private UserService userService;

    private EstrategiaCotizacionSiempre2MapAJPADTO estrategiaADTO = new EstrategiaCotizacionSiempre2MapAJPADTO();

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        // emitTokensForNewPlayer (disparado al crear un jugador) necesita que exista el
        // superadmin; otra clase de test puede haber borrado todos los usuarios antes que esta.
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO);
        userService.crearSuperusuario();
    }

    @AfterEach
    void teardown() {
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO);
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Helpers de apoyo
    // ────────────────────────────────────────────────────────────────────────────

    private EstrategiaCotizacion guardarEstrategiaEnContexto() {
        return estrategiaCotizacionService.guardar(ESTRATEGIA_1);
    }

    private EstrategiaCotizacion crearConfiguracionCotizacionesConEstrategiaSecundaria() {
        return configuracionCotizacionesServiceImpl.crearConfiguracionSiNoExiste(new ConfiguracionCotizaciones(ESTRATEGIA_2))
                    .getEstrategiaCotizacion();

    }

    private EstrategiaCotizacion crearConfiguracionCotizacionesConEstrategia() {
        return configuracionCotizacionesServiceImpl.crearConfiguracionSiNoExiste(new ConfiguracionCotizaciones(ESTRATEGIA_1))
                    .getEstrategiaCotizacion();

    }

    // ────────────────────────────────────────────────────────────────────────────
    // US1 — Consultar estrategias disponibles
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornarListaVaciaCuandoNoHayEstrategias() {
        ResponseEntity<List<EstrategiaCotizacionResponseDTO>> respuesta = restClient.get()
                .uri(BASE_PATH)
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());
    }

    @Test
    void debeListarTodasLasEstrategiasCuandoExisten() {
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();

        ResponseEntity<List<EstrategiaCotizacionResponseDTO>> respuesta = restClient.get()
                .uri(BASE_PATH)
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());

        EstrategiaCotizacionResponseDTO dto = respuesta.getBody().getFirst();
        assertEquals(estrategiaGuardada.getId(), dto.id());
        assertEquals(estrategiaGuardada.getValorBase(), dto.valorBase());
        assertEquals(estrategiaGuardada.getFactorEscala(), dto.factorEscala());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // US2 — Seleccionar estrategia activa
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void debeSeleccionarEstrategiaExitosamente() {

        crearConfiguracionCotizacionesConEstrategiaSecundaria();

        // Necesitamos primero una configuración activa, creamos la estrategia y la seleccionamos
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();

        // Creamos una configuración inicial con la misma estrategia

            // Seleccionamos la estrategia vía HTTP
            ResponseEntity<Void> respuesta = restClient.post()
                    .uri(BASE_PATH + "/" + estrategiaGuardada.getId() + "/select")
                    .header(HEADER_API_KEY, apiKey)
                    .retrieve()
                    .toBodilessEntity();

            assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void debeMantenerEstrategiaAlSeleccionarLaMisma() {
        crearConfiguracionCotizacionesConEstrategiaSecundaria();

        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();

            // Primera selección
            restClient.post()
                    .uri(BASE_PATH + "/" + estrategiaGuardada.getId() + "/select")
                    .header(HEADER_API_KEY, apiKey)
                    .retrieve()
                    .toBodilessEntity();

            // Segunda selección (misma estrategia, debe seguir siendo OK)
            ResponseEntity<Void> respuesta = restClient.post()
                    .uri(BASE_PATH + "/" + estrategiaGuardada.getId() + "/select")
                    .header(HEADER_API_KEY, apiKey)
                    .retrieve()
                    .toBodilessEntity();

            assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void debeRetornarEstrategiaSiExiste() {

        crearConfiguracionCotizacionesConEstrategiaSecundaria();

        // Necesitamos primero una configuración activa, creamos la estrategia y la seleccionamos
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();

        estrategiaCotizacionService.seleccionarEstrategia(estrategiaGuardada.getId());

        // Seleccionamos la estrategia vía HTTP
        ResponseEntity<EstrategiaCotizacionResponseDTO> respuesta = restClient.get()
                .uri(BASE_PATH + "/selected")
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(EstrategiaCotizacionResponseDTO.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(estrategiaGuardada.getId(), respuesta.getBody().id());
    }

    @Test
    void debeRetornarNotFoundAlNoHaberEstrategia() {
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.get()
                        .uri(BASE_PATH + "/selected")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toEntity(EstrategiaCotizacionResponseDTO.class)
        );
    }

    @Test
    void debeRetornarNotFoundAlHaberEstrategiaInexistente() {
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/" + -1L + "/select")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRetornarBadRequestConIdInvalido() {

        crearConfiguracionCotizacionesConEstrategiaSecundaria();

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/-1/select")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // US4 — Actualizar factor de escala de una estrategia
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void debeActualizarFactorEscalaExitosamente() {
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();
        ActualizarFactorEscalaRequestDTO request = new ActualizarFactorEscalaRequestDTO(2.5);

        ResponseEntity<Void> respuesta = restClient.patch()
                .uri(BASE_PATH + "/" + estrategiaGuardada.getId())
                .header(HEADER_API_KEY, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void debeFallarAlActualizarConFactorEscalaCeroONegativo() {
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();

        // Factor escala = 0
        ActualizarFactorEscalaRequestDTO requestCero = new ActualizarFactorEscalaRequestDTO(0.0);
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.patch()
                        .uri(BASE_PATH + "/" + estrategiaGuardada.getId())
                        .header(HEADER_API_KEY, apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestCero)
                        .retrieve()
                        .toBodilessEntity()
        );

        // Factor escala negativo
        ActualizarFactorEscalaRequestDTO requestNegativo = new ActualizarFactorEscalaRequestDTO(-1.0);
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.patch()
                        .uri(BASE_PATH + "/" + estrategiaGuardada.getId())
                        .header(HEADER_API_KEY, apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestNegativo)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRetornarNotFoundAlActualizarEstrategiaInexistente() {
        ActualizarFactorEscalaRequestDTO request = new ActualizarFactorEscalaRequestDTO(2.5);

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.patch()
                        .uri(BASE_PATH + "/" + -1L)
                        .header(HEADER_API_KEY, apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // US5 — Recotizar jugadores
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void debeCotizarJugadoresYActualizarPreciosDeMercado() {
        // Sembrar jugadores
        playerService.crear(getJugadorConNombre("Pepito"));
        playerService.crear(getJugadorConNombre("Juanito"));


        crearConfiguracionCotizacionesConEstrategia();

        // Disparar cotización vía HTTP
        ResponseEntity<String> respuesta = restClient.post()
                .uri(BASE_PATH + "/recalculate")
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().contains("Cotización de jugadores finalizada exitosamente"));
        playerService.recuperarTodos().forEach(
                player -> {
                    assertEquals(2.0, player.getCurrentPrice(), 0.1);
                }
        );
    }

    @Test
    void debeCotizarSinErrorCuandoNoHayJugadores() {

        crearConfiguracionCotizacionesConEstrategiaSecundaria();

        // Sin jugadores en base
        EstrategiaCotizacion estrategiaGuardada = guardarEstrategiaEnContexto();


        estrategiaCotizacionService.seleccionarEstrategia(estrategiaGuardada.getId());

        ResponseEntity<String> respuesta = restClient.post()
                .uri(BASE_PATH + "/recalculate")
                .header(HEADER_API_KEY, apiKey)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void debeRetornarNotFoundAlCotizarSinEstrategiaActiva() {
        // No hay ninguna estrategia seleccionada
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/recalculate")
                        .header(HEADER_API_KEY, apiKey)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // US6 — Control de acceso: rechazo sin API key válida
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void debeRechazarPeticionesSinApiKeyCon401() {
        // GET /estrategias-cotizacion sin API key
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.get()
                        .uri(BASE_PATH)
                        .retrieve()
                        .toBodilessEntity()
        );

        // POST /recalculate sin API key
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/recalculate")
                        .retrieve()
                        .toBodilessEntity()
        );

        // POST /{playerId}/select sin API key
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/1/select")
                        .retrieve()
                        .toBodilessEntity()
        );

        // PATCH /{playerId} sin API key
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.patch()
                        .uri(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ActualizarFactorEscalaRequestDTO(1.0))
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void debeRechazarPeticionesConApiKeyInvalidaCon401() {
        // GET con clave inválida
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.get()
                        .uri(BASE_PATH)
                        .header(HEADER_API_KEY, API_KEY_INVALIDA)
                        .retrieve()
                        .toBodilessEntity()
        );

        // POST /recalculate con clave inválida
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/recalculate")
                        .header(HEADER_API_KEY, API_KEY_INVALIDA)
                        .retrieve()
                        .toBodilessEntity()
        );

        // POST /{playerId}/select con clave inválida
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.post()
                        .uri(BASE_PATH + "/1/select")
                        .header(HEADER_API_KEY, API_KEY_INVALIDA)
                        .retrieve()
                        .toBodilessEntity()
        );

        // PATCH /{playerId} con clave inválida
        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
                restClient.patch()
                        .uri(BASE_PATH + "/1")
                        .header(HEADER_API_KEY, API_KEY_INVALIDA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ActualizarFactorEscalaRequestDTO(1.0))
                        .retrieve()
                        .toBodilessEntity()
        );
    }
}
