package com.overcode.e2e;

import com.overcode.config.DataInitializer;
import com.overcode.controller.dto.auth.AuthResponseDTO;
import com.overcode.controller.dto.auth.RegisterRequestDTO;
import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.controller.dto.bonus.mission.MissionStatusResponseDTO;
import com.overcode.controller.dto.bonus.mission.TipoMision;
import com.overcode.controller.dto.market.BuyTokenRequestDTO;
import com.overcode.controller.dto.user.DepositRequestDTO;
import com.overcode.model.player.Player;
import com.overcode.service.interfaces.PlayerService;
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
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static com.overcode.testUtils.bonus.TestBonusUtil.getMisionesTotalesEnArbol;
import static com.overcode.testUtils.bonus.TestBonusUtil.getObjetivosVistosEnMision;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DateMockConfiguration.class)
class DailyBonusBuyE2eTest {

    private static final String USERNAME = "marcelo";
    private static final String EMAIL = "marcelo@example.com";
    private static final String PASSWORD = "Password123!";

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;
    private String token;

    @Autowired
    private RequestDate requestDate;
    @Autowired
    private DataInitializer dataInitializer;
    @Autowired
    private PlayerService playerService;

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

        crearConfig();

        register();
    }

    private void crearConfig() {

        dataInitializer.initialize();
    }

    private void register() {
        RegisterRequestDTO registerReq = new RegisterRequestDTO(USERNAME, EMAIL, PASSWORD);
        ResponseEntity<AuthResponseDTO> registerRes = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerReq)
                .retrieve()
                .toEntity(AuthResponseDTO.class);

        this.token = registerRes.getBody().token();

        restClient.post()
                .uri("/users/deposit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DepositRequestDTO(1000.0))
                .retrieve()
                .toBodilessEntity();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarJugadoresYEquipos();
    }


    private Player crearJugador(String nombre, Double precio) {
        Player player = getJugadorConNombre(nombre);
        player.setCurrentPrice(precio);
        return playerService.crear(player);
    }

    private BuyTokenRequestDTO crearBuyRequest(Long playerId, Integer quantity) {
        return new BuyTokenRequestDTO(playerId, quantity);
    }

    @Test
    void alComprarPermiteReclamarBonusSiNoHayJugadores() {

        Player player = crearJugador("Lionel Messi", 20d);

        LocalDate hoy = LocalDate.of(2026, 10, 20);
        requestDate.setDate(hoy);

        restClient.post()
                .uri("/orders/buy")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearBuyRequest(player.getId(), 1))
                .retrieve()
                .toBodilessEntity();

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
    void consultarDatosDeBonusTraeDeEstaMision() {
        ResponseEntity<DailyBonusStatusResponseDTO> estado = restClient.get()
                .uri("/api/daily-bonus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(DailyBonusStatusResponseDTO.class);

        assertNotNull(estado.getBody());
        assertNotNull(estado.getBody().misiones());
        MissionStatusResponseDTO mision = estado.getBody().misiones();

        assertEquals(TipoMision.OBJECTIVE, mision.tipoMision());
        assertEquals(1, getMisionesTotalesEnArbol(mision));
        assertEquals(1, getObjetivosVistosEnMision(mision));
    }



}
