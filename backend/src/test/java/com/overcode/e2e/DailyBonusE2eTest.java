//package com.overcode.e2e;
//
//import com.overcode.controller.dto.auth.AuthResponse;
//import com.overcode.controller.dto.auth.LoginRequest;
//import com.overcode.controller.dto.auth.RegisterRequest;
//import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
//import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
//import com.overcode.model.bonus.DiaDeLaSemana;
//import com.overcode.model.bonus.LoginBonus;
//import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
//import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
//import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
//import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
//import com.overcode.persistence.repository.interfaces.UserRepository;
//import com.overcode.testUtils.TestLoginBonusUtil;
//import com.overcode.testUtils.TestService;
//import jakarta.annotation.PostConstruct;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestClient;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class DailyBonusE2eTest {
//
//    private static final String USERNAME = "marcelo";
//    private static final String EMAIL = "marcelo@example.com";
//    private static final String PASSWORD = "Password123!";
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestService testService;
//
//    @Autowired
//    private LoginBonusRepository loginBonusRepository;
//
//    @Autowired
//    private ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private RestClient restClient;
//    private String token;
//
//    @PostConstruct
//    void init() {
//        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
//    }
//
//    @BeforeEach
//    void setUp() {
//        testService.eliminarUsuarios();
//        testService.eliminarLoginBonuses();
//
//        LoginBonus baseBonus = loginBonusRepository.guardar(TestLoginBonusUtil.getLoginBonusBase());
//
//        ConfiguracionCotizaciones config = configuracionCotizacionesRepository.recuperarPorId(1L)
//                .orElse(new ConfiguracionCotizaciones(1L, new EstrategiaCotizacionRendimientoTotal(), baseBonus));
//        if (config.getEstrategiaCotizacion() == null) {
//            config.setEstrategiaCotizacion(new EstrategiaCotizacionRendimientoTotal());
//        }
//        config.setId(1L);
//        config.setLoginBonus(baseBonus);
//        configuracionCotizacionesRepository.guardar(config);
//
//        RegisterRequest registerReq = new RegisterRequest(USERNAME, EMAIL, PASSWORD);
//        ResponseEntity<AuthResponse> registerRes = restClient.post()
//                .uri("/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(registerReq)
//                .retrieve()
//                .toEntity(AuthResponse.class);
//
//        assertNotNull(registerRes.getBody());
//        this.token = registerRes.getBody().token();
//    }
//
//    @AfterEach
//    void tearDown() {
//        testService.eliminarUsuarios();
//        testService.eliminarLoginBonuses();
//    }
//
//    @Test
//    void alReclamarBonusDia1YDia2AvanzaLaRachaYElLoginNoOtorgaBonus() {
//        // Verificar que el login no otorga saldo ni modifica racha
//        LoginRequest loginReq = new LoginRequest(EMAIL, PASSWORD);
//        ResponseEntity<AuthResponse> loginRes = restClient.post()
//                .uri("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(loginReq)
//                .retrieve()
//                .toEntity(AuthResponse.class);
//
//        assertNotNull(loginRes.getBody());
//        assertEquals(0.0, loginRes.getBody().user().creditBalance());
//
//        LocalDate dia1 = LocalDate.of(2026, 10, 20);
//        ResponseEntity<ClaimBonusResponseDTO> claim1 = restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + dia1)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, claim1.getStatusCode());
//        assertNotNull(claim1.getBody());
//        assertEquals(1.0, claim1.getBody().getCreditsAwarded());
//        assertEquals(1, claim1.getBody().getNewStreak());
//        assertEquals(1.0, claim1.getBody().getCurrentCreditBalance());
//        assertEquals(dia1, claim1.getBody().getClaimDate());
//
//        LocalDate dia2 = LocalDate.of(2026, 10, 21);
//        ResponseEntity<ClaimBonusResponseDTO> claim2 = restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + dia2)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, claim2.getStatusCode());
//        assertNotNull(claim2.getBody());
//        assertEquals(2.0, claim2.getBody().getCreditsAwarded());
//        assertEquals(2, claim2.getBody().getNewStreak());
//        assertEquals(3.0, claim2.getBody().getCurrentCreditBalance());
//        assertEquals(dia2, claim2.getBody().getClaimDate());
//    }
//
//    @Test
//    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
//        LocalDate dia1 = LocalDate.of(2026, 10, 15);
//        restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + dia1)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        // Salto de 3 días
//        LocalDate dia4 = LocalDate.of(2026, 10, 18);
//        ResponseEntity<ClaimBonusResponseDTO> claimTrasInactividad = restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + dia4)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, claimTrasInactividad.getStatusCode());
//        assertNotNull(claimTrasInactividad.getBody());
//        assertEquals(1.0, claimTrasInactividad.getBody().getCreditsAwarded());
//        assertEquals(1, claimTrasInactividad.getBody().getNewStreak());
//        assertEquals(2.0, claimTrasInactividad.getBody().getCurrentCreditBalance());
//    }
//
//    @Test
//    void alCompletar7DiasDeRachaElDia8ReiniciaElCicloSemanal() {
//        LocalDate inicio = LocalDate.of(2026, 10, 1);
//        for (int i = 0; i < 7; i++) {
//            restClient.post()
//                    .uri("/api/daily-bonus/claim?fechaActual=" + inicio.plusDays(i))
//                    .header("Authorization", "Bearer " + token)
//                    .retrieve()
//                    .toEntity(ClaimBonusResponseDTO.class);
//        }
//
//        LocalDate dia8 = inicio.plusDays(7);
//        ResponseEntity<ClaimBonusResponseDTO> claimDia8 = restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + dia8)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, claimDia8.getStatusCode());
//        assertNotNull(claimDia8.getBody());
//        assertEquals(1.0, claimDia8.getBody().getCreditsAwarded());
//        assertEquals(1, claimDia8.getBody().getNewStreak());
//    }
//
//    @Test
//    void alIntentarReclamarDosVecesElMismoDiaRetorna400BadRequest() {
//        LocalDate hoy = LocalDate.of(2026, 10, 22);
//
//        ResponseEntity<ClaimBonusResponseDTO> primerReclamo = restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + hoy)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, primerReclamo.getStatusCode());
//
//        HttpClientErrorException.BadRequest ex = assertThrows(
//                HttpClientErrorException.BadRequest.class,
//                () -> restClient.post()
//                        .uri("/api/daily-bonus/claim?fechaActual=" + hoy)
//                        .header("Authorization", "Bearer " + token)
//                        .retrieve()
//                        .toBodilessEntity()
//        );
//
//        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
//        assertTrue(ex.getResponseBodyAsString().contains("El bonus diario de hoy ya ha sido reclamado."));
//    }
//
//    @Test
//    void alConsultarEstadoDeBonusAntesYDespuesDeReclamarRetornaValoresEsperados() {
//        LocalDate hoy = LocalDate.of(2026, 10, 22);
//
//        ResponseEntity<DailyBonusStatusResponseDTO> estadoAntes = restClient.get()
//                .uri("/api/daily-bonus?fechaActual=" + hoy)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(DailyBonusStatusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, estadoAntes.getStatusCode());
//        assertNotNull(estadoAntes.getBody());
//        assertEquals(0, estadoAntes.getBody().getCurrentStreak());
//        assertEquals(1.0, estadoAntes.getBody().getTodayBonus());
//        assertFalse(estadoAntes.getBody().getAlreadyClaimedToday());
//        assertEquals(7, estadoAntes.getBody().getWeeklyBonuses().size());
//        assertEquals(1.0, estadoAntes.getBody().getWeeklyBonuses().get(DiaDeLaSemana.LUNES));
//        assertEquals(15.0, estadoAntes.getBody().getWeeklyBonuses().get(DiaDeLaSemana.DOMINGO));
//
//        restClient.post()
//                .uri("/api/daily-bonus/claim?fechaActual=" + hoy)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(ClaimBonusResponseDTO.class);
//
//        ResponseEntity<DailyBonusStatusResponseDTO> estadoDespues = restClient.get()
//                .uri("/api/daily-bonus?fechaActual=" + hoy)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .toEntity(DailyBonusStatusResponseDTO.class);
//
//        assertEquals(HttpStatus.OK, estadoDespues.getStatusCode());
//        assertNotNull(estadoDespues.getBody());
//        assertEquals(1, estadoDespues.getBody().getCurrentStreak());
//        assertEquals(1.0, estadoDespues.getBody().getTodayBonus());
//        assertTrue(estadoDespues.getBody().getAlreadyClaimedToday());
//    }
//
//    @Test
//    void alIntentarAccederSinTokenJwtRetornaForbidden() {
//        assertThrows(
//                HttpClientErrorException.Forbidden.class,
//                () -> restClient.get()
//                        .uri("/api/daily-bonus")
//                        .retrieve()
//                        .toBodilessEntity()
//        );
//
//        assertThrows(
//                HttpClientErrorException.Forbidden.class,
//                () -> restClient.post()
//                        .uri("/api/daily-bonus/claim")
//                        .retrieve()
//                        .toBodilessEntity()
//        );
//    }
//}
