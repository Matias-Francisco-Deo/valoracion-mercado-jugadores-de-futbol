package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.LoginRequest;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.testUtils.TestDailyBonusUtil;
import com.overcode.testUtils.TestPlayerUtil;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.time.DateMockConfiguration;
import com.overcode.testUtils.time.RequestDate;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DateMockConfiguration.class)
class DailyBonusE2eTest {

    private static final String USERNAME = "marcelo";
    private static final String EMAIL = "marcelo@example.com";
    private static final String PASSWORD = "Password123!";

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @Autowired
    private DailyBonusRepository dailyBonusRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;

    private RestClient restClient;
    private String token;
    private Player player1;
    private Player player2;
    private Player player3;

    @Autowired
    private RequestDate requestDate;

    @PostConstruct
    void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        player1 = playerRepository.guardar(TestPlayerUtil.crearJugador("Jugador1", 100.0));
        player2 = playerRepository.guardar(TestPlayerUtil.crearJugador("Jugador2", 100.0));
        player3 = playerRepository.guardar(TestPlayerUtil.crearJugador("Jugador3", 100.0));

        Visit3PlayersDailyBonusMission visitMission = new Visit3PlayersDailyBonusMission(
                new VisitPlayerDailyBonusMission(player1),
                new VisitPlayerDailyBonusMission(player2),
                new VisitPlayerDailyBonusMission(player3)
        );
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        BuyOrVisitMission dailyOr = new BuyOrVisitMission(buyMission, visitMission);

        DailyBonus bonusBase = dailyBonusRepository.guardar(TestDailyBonusUtil.getDailyBonusBase(dailyOr));

        ConfiguracionMercado config = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        config.setId(1L);
        config.setDailyBonus(bonusBase);
        configuracionMercadoService.crearConfiguracionSiNoExiste(config);

        RegisterRequest registerReq = new RegisterRequest(USERNAME, EMAIL, PASSWORD);
        ResponseEntity<AuthResponse> registerRes = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerReq)
                .retrieve()
                .toEntity(AuthResponse.class);

        assertNotNull(registerRes.getBody());
        this.token = registerRes.getBody().token();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void elLoginNoOtorgaBonus() {
        LoginRequest loginReq = new LoginRequest(EMAIL, PASSWORD);
        ResponseEntity<AuthResponse> loginRes = restClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthResponse.class);

        assertNotNull(loginRes.getBody());
        assertEquals(0.0, loginRes.getBody().user().creditBalance());
    }

    @Test
    void alIntentarReclamarSinCompletarMisionesRetorna400BadRequest() {
        LocalDate hoy = LocalDate.of(2026, 10, 20);
        requestDate.setDate(hoy);

        HttpClientErrorException.BadRequest ex = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> restClient.post()
                        .uri("/api/daily-bonus/claim")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getResponseBodyAsString().contains("No se han cumplido los requisitos"));
    }

    @Test
    void alCompletar3VisitasPermiteReclamarBonusCorrectamente() {
        LocalDate hoy = LocalDate.of(2026, 10, 20);
        requestDate.setDate(hoy);

        // Visitar los 3 jugadores
        restClient.post()
                .uri("/api/daily-bonus/visit/" + player1.getId())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        restClient.post()
                .uri("/api/daily-bonus/visit/" + player2.getId())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<DailyBonusStatusResponseDTO> visit3 = restClient.post()
                .uri("/api/daily-bonus/visit/" + player3.getId())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertTrue(visit3.getBody().puedeReclamar());

        ResponseEntity<ClaimBonusResponseDTO> claimRes = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, claimRes.getStatusCode());
        assertNotNull(claimRes.getBody());
        assertEquals(1.0, claimRes.getBody().givenBonus());
        assertEquals(1, claimRes.getBody().currentStreak());
    }

    @Test
    void alReclamarBonusDia1YDia2AvanzaLaRacha() {
        LocalDate dia1 = LocalDate.of(2026, 10, 20);
        requestDate.setDate(dia1);

        restClient.post().uri("/api/daily-bonus/visit/" + player1.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player2.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player3.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();

        ResponseEntity<ClaimBonusResponseDTO> claim1 = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, claim1.getStatusCode());
        assertNotNull(claim1.getBody());
        assertEquals(1.0, claim1.getBody().givenBonus());
        assertEquals(1, claim1.getBody().currentStreak());

        LocalDate dia2 = LocalDate.of(2026, 10, 21);
        requestDate.setDate(dia2);

        restClient.post().uri("/api/daily-bonus/visit/" + player1.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player2.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player3.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();

        ResponseEntity<ClaimBonusResponseDTO> claim2 = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, claim2.getStatusCode());
        assertNotNull(claim2.getBody());
        assertEquals(2.0, claim2.getBody().givenBonus());
        assertEquals(2, claim2.getBody().currentStreak());
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate dia1 = LocalDate.of(2026, 10, 15);
        requestDate.setDate(dia1);

        restClient.post().uri("/api/daily-bonus/visit/" + player1.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player2.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player3.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();

        restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        // Salto de 3 días
        LocalDate dia4 = LocalDate.of(2026, 10, 18);
        requestDate.setDate(dia4);

        restClient.post().uri("/api/daily-bonus/visit/" + player1.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player2.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player3.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();

        ResponseEntity<ClaimBonusResponseDTO> claimTrasInactividad = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, claimTrasInactividad.getStatusCode());
        assertNotNull(claimTrasInactividad.getBody());
        assertEquals(1.0, claimTrasInactividad.getBody().givenBonus());
        assertEquals(1, claimTrasInactividad.getBody().currentStreak());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaRetorna400BadRequest() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        requestDate.setDate(hoy);

        restClient.post().uri("/api/daily-bonus/visit/" + player1.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player2.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
        restClient.post().uri("/api/daily-bonus/visit/" + player3.getId()).header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();

        ResponseEntity<ClaimBonusResponseDTO> primerReclamo = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, primerReclamo.getStatusCode());

        HttpClientErrorException.BadRequest ex = assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> restClient.post()
                        .uri("/api/daily-bonus/claim")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getResponseBodyAsString().contains("El bonus diario de hoy ya ha sido reclamado."));
    }

    @Test
    void consultarDatosDeBonusEsConsistenteConModelo() {
        ResponseEntity<DailyBonusStatusResponseDTO> estado = restClient.get()
                .uri("/api/daily-bonus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertEquals(HttpStatus.OK, estado.getStatusCode());
        assertNotNull(estado.getBody());
        assertEquals(7, estado.getBody().weeklyBonusRewards().size());
        assertEquals(1.0, estado.getBody().weeklyBonusRewards().get(0));
        assertEquals(15.0, estado.getBody().weeklyBonusRewards().get(6));
        assertNotNull(estado.getBody().misiones());
        assertFalse(estado.getBody().misiones().isEmpty());
    }

    @Test
    void alIntentarAccederSinTokenJwtRetornaForbidden() {
        assertThrows(
                HttpClientErrorException.Forbidden.class,
                () -> restClient.get()
                        .uri("/api/daily-bonus")
                        .retrieve()
                        .toBodilessEntity()
        );

        assertThrows(
                HttpClientErrorException.Forbidden.class,
                () -> restClient.post()
                        .uri("/api/daily-bonus/claim")
                        .retrieve()
                        .toBodilessEntity()
        );
    }
}
