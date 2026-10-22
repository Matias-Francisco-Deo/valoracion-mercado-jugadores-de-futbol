package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.position.PositionResponseDTO;
import com.overcode.controller.dto.token.SummaryTokenPageResponseDTO;
import com.overcode.controller.dto.transaction.TransactionPageResponseDTO;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.model.player.Player;
import com.overcode.model.transaction.Transaction;
import com.overcode.model.transaction.TransactionType;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 class UserE2eTest {

    private static final String DEFAULT_USERNAME = "userTestUser";
    private static final String DEFAULT_EMAIL = "usertest@example.com";
    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String SECOND_USERNAME = "otherUser";
    private static final String SECOND_EMAIL = "other@example.com";
    private static final String SECOND_PASSWORD = "OtherPassword123!";
    private static final Long NON_EXISTENT_ID = 999999L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RestClient restClient;

    @PostConstruct
     void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private AuthResponse registerUser(String username, String email, String password) {
        var req = new RegisterRequest(username, email, password);
        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);

        return response.getBody();
    }

    private Player crearJugador(String nombre, Double precio) {
        Player player = new Player();
        player.setName(nombre);
        player.setCurrentPrice(precio);
        return playerRepository.guardar(player);
    }

    private void asignarTokensAUsuario(Long userId, Long playerId, int cantidad) {
        jdbcTemplate.update(
                "UPDATE tokens SET owner_id = ? WHERE id IN (" +
                        "SELECT id FROM tokens WHERE player_id = ? AND owner_id IS NULL LIMIT ?)",
                userId, playerId, cantidad
        );
    }

    private void registrarTransaccion(Long userId, Player player, TransactionType type, int quantity) {
        User user = new User(userId, DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD, 0d, new ArrayList<>());
        Transaction tx = new Transaction(user, player, type, quantity);
        transactionRepository.guardar(tx);
    }

    private void limpiarBaseDeDatos() {
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM sell_orders");
        jdbcTemplate.execute("DELETE FROM token_holding");
        jdbcTemplate.execute("DELETE FROM portfolio");
        jdbcTemplate.execute("DELETE FROM tokens");
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarUsuarios();
    }

    @BeforeEach
    void setUp() {
        limpiarBaseDeDatos();
    }

    @AfterEach
    void tearDown() {
        limpiarBaseDeDatos();
    }

    // ------------------------------ Tests de consulta de usuario por ID ------------------------------

    @Test
     void obtenerUsuarioPorIdExistenteDevuelveOkConDatosCorrectos() {
        var authUser1 = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        var authUser2 = registerUser(SECOND_USERNAME, SECOND_EMAIL, SECOND_PASSWORD);
        Long user1Id = authUser1.user().id();

        ResponseEntity<UserResponseDTO> response = restClient.get()
            .uri("/users/" + user1Id)
            .header("Authorization", "Bearer " + authUser2.token())
            .retrieve()
            .toEntity(UserResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user1Id, response.getBody().id());
        assertEquals(DEFAULT_USERNAME, response.getBody().username());
        assertEquals(DEFAULT_EMAIL, response.getBody().email());
    }

    @Test
     void obtenerUsuarioPorIdNoExponeContrasenaNiCredenciales() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        ResponseEntity<String> response = restClient.get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().contains("password"));
        assertFalse(response.getBody().contains(DEFAULT_PASSWORD));
    }

    @Test
     void obtenerUsuarioPorIdInexistenteLanzaNotFound() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/users/" + NON_EXISTENT_ID)
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toBodilessEntity());
    }

    // ============================================================================================
    // ----------------------- Tests de consulta de tokens de usuario (/tokens) --------------------
    // ============================================================================================

    @Test
    void buscarSummaryTokensPorUsuarioDevuelveTokensDelUsuario() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player player = crearJugador("Lionel Messi", 20d);
        asignarTokensAUsuario(userId, player.getId(), 5);

        ResponseEntity<SummaryTokenPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/tokens")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(SummaryTokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals(1, response.getBody().content().size());

        var tokenSummary = response.getBody().content().getFirst();
        assertEquals(player.getId(), tokenSummary.playerId());
        assertEquals("Lionel Messi", tokenSummary.playerName());
        assertEquals(5, tokenSummary.quantity());
        assertEquals(20.0, tokenSummary.unitPrice());
    }

    @Test
    void buscarSummaryTokensPorUsuarioSinTokensDevuelvePaginaVacia() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        ResponseEntity<SummaryTokenPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/tokens")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(SummaryTokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void buscarSummaryTokensPorUsuarioConFiltroNombreDevuelveTokensFiltrados() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player p1 = crearJugador("Cristiano Ronaldo", 30d);
        Player p2 = crearJugador("Lionel Messi", 25d);
        asignarTokensAUsuario(userId, p1.getId(), 3);
        asignarTokensAUsuario(userId, p2.getId(), 4);

        ResponseEntity<SummaryTokenPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/tokens?playerName=Messi")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(SummaryTokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals("Lionel Messi", response.getBody().content().getFirst().playerName());
        assertEquals(4, response.getBody().content().getFirst().quantity());
    }

    @Test
    void buscarSummaryTokensPorUsuarioConPaginacionPersonalizada() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player p1 = crearJugador("Player One", 10d);
        Player p2 = crearJugador("Player Two", 15d);
        asignarTokensAUsuario(userId, p1.getId(), 2);
        asignarTokensAUsuario(userId, p2.getId(), 3);

        ResponseEntity<SummaryTokenPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/tokens?page=0&size=1")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(SummaryTokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(2, response.getBody().totalElements());
        assertEquals(2, response.getBody().totalPages());
    }

    @Test
    void buscarDatosDeUserInexistenteDaNotFound() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () -> getDeUserInexistente(auth, "/tokens"));
        assertThrows(HttpClientErrorException.NotFound.class, () -> getDeUserInexistente(auth, "/transactions"));
        assertThrows(HttpClientErrorException.NotFound.class, () -> getDeUserInexistente(auth, "/portfolio"));
    }



    @Test
    void buscarSummaryTokensPorUsuarioSinAutenticacionDevuelveError() {
        // Flujo excepcional: endpoint protegido llamado sin token
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, this::getUser1Tokens);

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    private void getUser1Tokens() {
        restClient.get()
                .uri("/users/1/tokens")
                .retrieve()
                .toBodilessEntity();
    }

    // ============================================================================================
    // ------------------- Tests de consulta de transacciones (/transactions) ---------------------
    // ============================================================================================

    @Test
    void recuperarTransaccionesDeUsuarioDevuelveTransaccionesRealizadas() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player player = crearJugador("Erling Haaland", 50d);
        registrarTransaccion(userId, player, TransactionType.BUY, 2);

        ResponseEntity<TransactionPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/transactions")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(TransactionPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());

        var txResponse = response.getBody().content().getFirst();
        assertEquals("Erling Haaland", txResponse.playerName());
        assertEquals(2, txResponse.quantity());
        assertEquals(50.0, txResponse.unitPrice());
        assertEquals(100.0, txResponse.totalPrice());
        assertEquals(TransactionType.BUY, txResponse.type());
    }

    @Test
    void recuperarTransaccionesDeUsuarioSinTransaccionesDevuelvePaginaVacia() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        ResponseEntity<TransactionPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/transactions")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(TransactionPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void recuperarTransaccionesDeUsuarioConPaginacionPersonalizada() {
        // Caso borde: paginación de transacciones
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player player = crearJugador("Kylian Mbappe", 40d);
        registrarTransaccion(userId, player, TransactionType.BUY, 1);
        registrarTransaccion(userId, player, TransactionType.SELL, 1);

        ResponseEntity<TransactionPageResponseDTO> response = restClient.get()
                .uri("/users/" + userId + "/transactions?page=0&size=1")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(TransactionPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(2, response.getBody().totalElements());
        assertEquals(2, response.getBody().totalPages());
    }

    @Test
    void recuperarTransaccionesSinAutenticacionDevuelveError() {
        // Flujo excepcional: llamada sin credenciales
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, this::getTransaccionesUser1);

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    private @NonNull ResponseEntity<Void> getTransaccionesUser1() {
        return restClient.get()
                .uri("/users/1/transactions")
                .retrieve()
                .toBodilessEntity();
    }

    // ============================================================================================
    // ----------------------- Tests de consulta de portfolio (/portfolio) ------------------------
    // ============================================================================================

    @Test
    void recuperarPortfolioDeUsuarioDevuelvePosicionesCalculadas() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        Player player = crearJugador("Vinicius Jr", 60d);
        // Se registra compra de 4 tokens a precio 60
        registrarTransaccion(userId, player, TransactionType.BUY, 4);

        ResponseEntity<List<PositionResponseDTO>> response = restClient.get()
                .uri("/users/" + userId + "/portfolio")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        PositionResponseDTO position = response.getBody().getFirst();
        assertEquals(player.getId(), position.playerId());
        assertEquals("Vinicius Jr", position.playerName());
        assertEquals(4, position.quantity());
        assertEquals(60.0, position.averagePurchasePrice());
        assertEquals(60.0, position.currentPrice());
        assertEquals(240.0, position.currentValue());
        assertEquals(0.0, position.profitLoss());
        assertEquals(0.0, position.profitLossPercentage());
    }

    @Test
    void recuperarPortfolioDeUsuarioSinPosicionesDevuelveListaVacia() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        ResponseEntity<List<PositionResponseDTO>> response = restClient.get()
                .uri("/users/" + userId + "/portfolio")
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void recuperarPortfolioSinAutenticacionDevuelveError() {
        // Flujo excepcional: llamada sin credenciales
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, this::getPortfolioUser1);

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    private void getPortfolioUser1() {
        restClient.get()
                .uri("/users/1/portfolio")
                .retrieve()
                .toBodilessEntity();
    }

    private @NonNull ResponseEntity<Void> getDeUserInexistente(AuthResponse auth, String endpoint) {
        return restClient.get()
                .uri("/users/" + NON_EXISTENT_ID + endpoint)
                .header("Authorization", "Bearer " + auth.token())
                .retrieve()
                .toBodilessEntity();
    }
}
