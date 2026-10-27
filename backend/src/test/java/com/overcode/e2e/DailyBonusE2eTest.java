package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.LoginRequest;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.testUtils.DateMockConfiguration;
import com.overcode.testUtils.RequestDate;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestService;
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
    private LoginBonusRepository loginBonusRepository;

    @Autowired
    private ConfiguracionCotizacionesService configuracionCotizacionesService;

    private RestClient restClient;
    private String token;

    @Autowired
    private RequestDate requestDate;

    @PostConstruct
    void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarLoginBonuses();

        LoginBonus bonusBase = loginBonusRepository.guardar(TestLoginBonusUtil.getLoginBonusBase());

        ConfiguracionCotizaciones config = new ConfiguracionCotizaciones(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        config.setId(1L);
        config.setLoginBonus(bonusBase);
        configuracionCotizacionesService.crearConfiguracionSiNoExiste(config);

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
        testService.eliminarLoginBonuses();
    }

    @Test
    void elLoginNoOtorgaBonus() {
        // Verificar que el login no otorga saldo ni modifica racha
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
    void alReclamarBonusDia1YDia2AvanzaLaRacha() {

        LocalDate dia1 = LocalDate.of(2026, 10, 20);
        requestDate.setDate(dia1);
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
        restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        // Salto de 3 días
        LocalDate dia4 = LocalDate.of(2026, 10, 18);
        requestDate.setDate(dia4);
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
    void alCompletar7DiasDeRachaElDia8ReiniciaElCicloSemanal() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        for (int i = 0; i < 7; i++) {
            requestDate.setDate(inicio.plusDays(i));
            restClient.post()
                    .uri("/api/daily-bonus/claim" )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toEntity(ClaimBonusResponseDTO.class);
        }

        LocalDate dia8 = inicio.plusDays(7);
        requestDate.setDate(dia8);
        ResponseEntity<ClaimBonusResponseDTO> claimDia8 = restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        assertEquals(HttpStatus.OK, claimDia8.getStatusCode());
        assertNotNull(claimDia8.getBody());
        assertEquals(1.0, claimDia8.getBody().givenBonus());
        assertEquals(1, claimDia8.getBody().currentStreak());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaRetorna400BadRequest() {

        requestDate.setDate(LocalDate.of(2026, 10, 22));
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

        ResponseEntity<DailyBonusStatusResponseDTO> estadoAntes = restClient.get()
                .uri("/api/daily-bonus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertEquals(HttpStatus.OK, estadoAntes.getStatusCode());
        assertNotNull(estadoAntes.getBody());
        assertEquals(7, estadoAntes.getBody().weeklyBonusRewards().size());
        assertEquals(1.0, estadoAntes.getBody().weeklyBonusRewards().get(0));
        assertEquals(15.0, estadoAntes.getBody().weeklyBonusRewards().get(6));
    }

    @Test
    void alConsultarEstadoDeBonusAntesYDespuesDeReclamarRetornaValoresEsperados() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        requestDate.setDate(hoy);

        ResponseEntity<DailyBonusStatusResponseDTO> estadoAntes = restClient.get()
                .uri("/api/daily-bonus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertEquals(HttpStatus.OK, estadoAntes.getStatusCode());
        assertNotNull(estadoAntes.getBody());
        assertEquals(0, estadoAntes.getBody().currentStreak());
        assertEquals(1.0, estadoAntes.getBody().nextBonus());
        assertFalse(estadoAntes.getBody().alreadyClaimedToday());

        restClient.post()
                .uri("/api/daily-bonus/claim")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(ClaimBonusResponseDTO.class);

        ResponseEntity<DailyBonusStatusResponseDTO> estadoDespues = restClient.get()
                .uri("/api/daily-bonus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertEquals(HttpStatus.OK, estadoDespues.getStatusCode());
        assertNotNull(estadoDespues.getBody());
        assertEquals(1, estadoDespues.getBody().currentStreak());
        assertEquals(2.0, estadoDespues.getBody().nextBonus());
        assertTrue(estadoDespues.getBody().alreadyClaimedToday());
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
