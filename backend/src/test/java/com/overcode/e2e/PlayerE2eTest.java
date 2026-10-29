package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.cotizacion.RegistroCotizacionResponseDTO;
import com.overcode.controller.dto.player.PlayerPageResponseDTO;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.TokenEmissionService;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.rating.EstrategiaCotizacionTestRatingMapAJPADTO;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2MapAJPADTO;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.*;
import static com.overcode.testUtils.cotizacion.TestEstrategiaCotizacionUtil.getEstrategiaRating;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerE2eTest {

    private static final String DEFAULT_USERNAME = "playerTestUser";
    private static final String DEFAULT_EMAIL = "playertest@example.com";
    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String PLAYER_NAME = "Player1";
    private static final String SECOND_PLAYER_NAME = "Player2";
    private static final Long NON_EXISTENT_ID = -1L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @Autowired
    private PlayerService playerService;

    @MockitoBean
    private TokenEmissionService tokenEmissionService;

    private RestClient restClient;

    @Autowired
    private EstrategiaCotizacionService estrategiaCotizacionService;

    @Autowired
    private ConfiguracionCotizacionesService configuracionCotizacionesService;

    private static final EstrategiaCotizacionSiempre2 ESTRATEGIA_1 =
            new EstrategiaCotizacionSiempre2(0.0, 1.0);

    private final EstrategiaCotizacionSiempre2MapAJPADTO estrategiaADTO = new EstrategiaCotizacionSiempre2MapAJPADTO();
    private final EstrategiaCotizacionTestRatingMapAJPADTO estrategiaADTO2 = new EstrategiaCotizacionTestRatingMapAJPADTO();


    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private String obtainAuthToken() {
        var req = new RegisterRequest(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);
        return response.getBody().token();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarUsuarios();
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO);
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO2);
    }

    @AfterEach
    void tearDown() {
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO);
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO2);
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarUsuarios();
    }

    // ------------------------------ Tests de listado de jugadores ------------------------------

    @Test
    void listarJugadoresConBaseVaciaDevuelveListaVacia() {
        String token = obtainAuthToken();

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());

        assertEquals(0, response.getBody().totalElements());
        assertEquals(0, response.getBody().totalPages());
    }

    @Test
    void listarJugadoresConJugadoresExistentesDevuelveListaCompleta() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConNombre(PLAYER_NAME));
        playerService.crear(getJugadorConNombre(SECOND_PLAYER_NAME));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<PlayerResponseDTO> players = response.getBody().content();
        assertEquals(2, players.size());
        assertTrue(players.stream().anyMatch(p -> PLAYER_NAME.equals(p.name())));
        assertTrue(players.stream().anyMatch(p -> SECOND_PLAYER_NAME.equals(p.name())));
    }

    @Test
    void listarJugadoresConJugadoresExistentesConFiltroPorClubDevuelveDeEseClub() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConClub(PLAYER_NAME, "Club1"));
        playerService.crear(getJugadorConClub(SECOND_PLAYER_NAME, "Club2"));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players?clubName=Club1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PlayerResponseDTO> players = response.getBody().content();

        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());

        assertEquals(1, response.getBody().totalElements());
        assertEquals(1, response.getBody().totalPages());
    }

    @Test
    void listarJugadoresConJugadoresExistentesConFiltroPorClubNameCamelCaseDevuelveDeEseClub() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConClub(PLAYER_NAME, "ClubUno"));
        playerService.crear(getJugadorConClub(SECOND_PLAYER_NAME, "ClubDos"));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players?clubName=ClubUno")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PlayerResponseDTO> players = response.getBody().content();

        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarJugadoresConJugadoresExistentesConFiltroPorLigaDevuelveDeEsaLiga() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConLigaYClub(PLAYER_NAME, "Liga1", "Club1"));
        playerService.crear(getJugadorConLigaYClub(SECOND_PLAYER_NAME, "Liga2", "Club2"));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players?league=Liga1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PlayerResponseDTO> players = response.getBody().content();

        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarJugadoresConFiltroPorPosicionDevuelveDeEsaPosicion() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConPosicion(PLAYER_NAME, PlayerPosition.DEFENSOR));
        playerService.crear(getJugadorConPosicion(SECOND_PLAYER_NAME, PlayerPosition.ATACANTE));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players?position=DEFENSOR")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PlayerResponseDTO> players = response.getBody().content();

        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
        assertEquals(PlayerPosition.DEFENSOR, players.getFirst().playerGameData().position());
    }

    @Test
    void ligarJugadoresPorVariosFiltros() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConLigaYClub(PLAYER_NAME, "Liga1", "Club1"));
        playerService.crear(getJugadorConLigaYClub(SECOND_PLAYER_NAME, "Liga2", "Club2"));
        playerService.crear(getJugadorConLigaYClub("Jugador3", "Liga2", "Club3"));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players?clubName=Club1&league=Liga1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<PlayerResponseDTO> players = response.getBody().content();

        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarRankingDeJugadoresTraeOrdenadosPorRating() {
        String token = obtainAuthToken();

        configuracionCotizacionesService.crearConfiguracionSiNoExiste(new ConfiguracionCotizaciones(getEstrategiaRating())); // esta estrategia discrimina por rating only

        Player player1 = playerService.crear(getJugadorConRating(PLAYER_NAME, 9.5));
        Player player2 = playerService.crear(getJugadorConRating(SECOND_PLAYER_NAME, 6.0));

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players/ranking")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlayerPageResponseDTO page = response.getBody();

        assertNotNull(page);
        List<PlayerResponseDTO> players = page.content();
        assertEquals(2, players.size());
        assertEquals(players.get(0).id(), player1.getId());
        assertEquals(players.get(1).id(), player2.getId());

    }


    @Test
    void listarRankingJugadoresSinJugadoresDaVacio() {
        String token = obtainAuthToken();

        ResponseEntity<PlayerPageResponseDTO> response = restClient.get()
                .uri("/players/ranking")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlayerPageResponseDTO page = response.getBody();

        assertNotNull(page);
        List<PlayerResponseDTO> players = page.content();
        assertTrue(players.isEmpty());

    }

    // ------------------------------ Tests de consulta de jugador por ID ------------------------------

    @Test
    void obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos() {
        String token = obtainAuthToken();
        Player guardado = playerService.crear(getJugadorConNombre(PLAYER_NAME));

        ResponseEntity<PlayerResponseDTO> response = restClient.get()
            .uri("/players/" + guardado.getId())
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(PlayerResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(guardado.getId(), response.getBody().id());
        assertEquals(PLAYER_NAME, response.getBody().name());
        assertEquals(1, response.getBody().currentPrice()); // como es recién creado, esto es válido
        assertEquals(100, response.getBody().tokens().size());
    }

    // -------------------------------- Tests de registros de cotizaciones ---------------------------------

    @Test
    void seCotizaJugadorYSePuedenRecuperarRegistros() {

        LocalDateTime inicio = LocalDateTime.now();

        configuracionCotizacionesService.crearConfiguracionSiNoExiste(
                new ConfiguracionCotizaciones(ESTRATEGIA_1)
        );

        String token = obtainAuthToken();
        Player guardado = playerService.crear(getJugadorConNombre(PLAYER_NAME));
        estrategiaCotizacionService.cotizarJugadores();

        LocalDateTime fin = LocalDateTime.now();

        ResponseEntity<List<RegistroCotizacionResponseDTO>> response = restClient.get()
                .uri("/players/" + guardado.getId() + "/quotes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RegistroCotizacionResponseDTO> body = response.getBody();
        assertNotNull(body);
        RegistroCotizacionResponseDTO registroPrimero = body.getFirst();
        assertEquals(guardado.getId(), registroPrimero.player().id());

        // se usa la estrategia mock siempre 2, así que tiene que ser 2
        assertEquals(2, registroPrimero.cotizacion());
        assertEquals(2, registroPrimero.player().currentPrice());
        assertTrue(registroPrimero.createdAt().isAfter(inicio));
        assertTrue(registroPrimero.createdAt().isBefore(fin));
        assertEquals(ESTRATEGIA_1.getTitulo(), registroPrimero.estrategiaCotizacionUsada().titulo());
    }

    @Test
    void seCotizaJugadorYSePuedenRecuperarRegistrosEnRango() {

        LocalDateTime inicio = LocalDateTime.now();

        configuracionCotizacionesService.crearConfiguracionSiNoExiste(
                new ConfiguracionCotizaciones(ESTRATEGIA_1)
        );

        String token = obtainAuthToken();
        Player guardado = playerService.crear(getJugadorConNombre(PLAYER_NAME));
        estrategiaCotizacionService.cotizarJugadores();

        LocalDateTime fin = LocalDateTime.now().plusSeconds(20);

        ResponseEntity<List<RegistroCotizacionResponseDTO>> response = restClient.get()
                .uri("/players/" + guardado.getId() + "/quotes?startDate=" + inicio + "&endDate=" + fin)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RegistroCotizacionResponseDTO> body = response.getBody();
        assertNotNull(body);
        RegistroCotizacionResponseDTO registroPrimero = body.getFirst();
        assertEquals(guardado.getId(), registroPrimero.player().id());

        // se usa la estrategia mock siempre 2, así que tiene que ser 2
        assertEquals(2, registroPrimero.cotizacion());
        assertEquals(2, registroPrimero.player().currentPrice());
        assertTrue(registroPrimero.createdAt().isAfter(inicio));
        assertTrue(registroPrimero.createdAt().isBefore(fin));
        assertEquals(ESTRATEGIA_1.getTitulo(), registroPrimero.estrategiaCotizacionUsada().titulo());
    }

    @Test
    void seCotizaJugadorYSePuedenRecuperarVariosRegistrosEnRango() {

        LocalDateTime inicio = LocalDateTime.now();

        configuracionCotizacionesService.crearConfiguracionSiNoExiste(
                new ConfiguracionCotizaciones(ESTRATEGIA_1)
        );

        String token = obtainAuthToken();
        Player guardado = playerService.crear(getJugadorConNombre(PLAYER_NAME));
        estrategiaCotizacionService.cotizarJugadores();
        estrategiaCotizacionService.cotizarJugadores();
        estrategiaCotizacionService.cotizarJugadores();

        LocalDateTime fin = LocalDateTime.now();

        ResponseEntity<List<RegistroCotizacionResponseDTO>> response = restClient.get()
                .uri("/players/" + guardado.getId() + "/quotes?startDate=" + inicio + "&endDate=" + fin)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RegistroCotizacionResponseDTO> body = response.getBody();
        assertNotNull(body);

        assertEquals(3, body.size());
    }

    @Test
    void obtenerJugadorPorIdInexistenteLanzaNotFound() {
        String token = obtainAuthToken();

        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/players/" + NON_EXISTENT_ID)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    void syncMetricsSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.post()
            .uri("/players/sync-metrics")
            .retrieve()
            .toBodilessEntity());
    }

    private Player getJugadorConPosicion(String name, PlayerPosition position) {
        Player player = getJugadorConNombre(name);
        player.setPlayerGameData(new PlayerGameData(null, player, 0, 0, 0, 0, 0, 0, 1.0, position));
        return player;
    }

}
