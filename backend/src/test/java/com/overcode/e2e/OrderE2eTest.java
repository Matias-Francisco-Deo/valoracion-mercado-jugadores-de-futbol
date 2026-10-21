package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.controller.dto.p2p.OrderSellSummaryPageResponseDTO;
import com.overcode.model.Player;
import com.overcode.model.orden.OrderStatus;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.testUtils.TestService;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderE2eTest {

    private static final String SELLER_USERNAME = "orderSellerUser";
    private static final String SELLER_EMAIL = "orderseller@example.com";
    private static final String SELLER_PASSWORD = "Password123!";

    private static final String BUYER_USERNAME = "orderBuyerUser";
    private static final String BUYER_EMAIL = "orderbuyer@example.com";
    private static final String BUYER_PASSWORD = "Password123!";

    private static final Long NON_EXISTENT_ID = 999999L;

    record SellOrderRecord(
            Long id,
            Long userId,
            Long playerId,
            Long tokenId,
            Integer price,
            OrderStatus status,
            Instant createdAt
    ) {}

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RestClient restClient;

    @PostConstruct
    void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @BeforeEach
    void setUp() {
        limpiarBaseDeDatos();
    }

    @AfterEach
    void tearDown() {
        limpiarBaseDeDatos();
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

    private Player crearJugador(String nombre, int precio) {
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

    private void acreditarFondosAUsuario(Long userId, int creditos) {
        jdbcTemplate.update("UPDATE users SET credit_balance = ? WHERE id = ?", creditos, userId);
    }

    private BuyTokenRequest crearBuyRequest(Long playerId, Integer quantity) {
        BuyTokenRequest req = new BuyTokenRequest();
        req.setPlayerId(playerId);
        req.setQuantity(quantity);
        return req;
    }

    private SellTokenRequest crearSellRequest(Long playerId, Integer quantity) {
        SellTokenRequest req = new SellTokenRequest();
        req.setPlayerId(playerId);
        req.setQuantity(quantity);
        return req;
    }

    // ============================================================================================
    // --------------------------------- Tests de POST /orders/buy ---------------------------------
    // ============================================================================================

    @Test
    void comprarTokensExitosamenteCuandoHayLiquidezYCreditosSuficientes() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Lionel Messi", 20);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 5);

        // Vendedor publica 5 órdenes de venta mediante la API
        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 5))
                .retrieve()
                .toBodilessEntity();

        // Se otorgan créditos al comprador
        acreditarFondosAUsuario(buyerAuth.user().id(), 100);

        // Comprador adquiere 2 tokens
        ResponseEntity<Map<String, Object>> response = restClient.post()
                .uri("/orders/buy")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearBuyRequest(player.getId(), 2))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tokens successfully purchased", response.getBody().get("message"));
        assertEquals(player.getId().intValue(), ((Number) response.getBody().get("playerId")).intValue());
        assertEquals(2, ((Number) response.getBody().get("quantity")).intValue());

        // Se verifica que el comprador ahora tiene 2 tokens en base de datos
        Integer tokensComprador = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tokens WHERE owner_id = ? AND player_id = ?",
                Integer.class, buyerAuth.user().id(), player.getId()
        );
        assertEquals(2, tokensComprador);

        // Se verifica que el vendedor recibió 2 * 20 = 40 créditos
        Integer balanceVendedor = jdbcTemplate.queryForObject(
                "SELECT credit_balance FROM users WHERE id = ?",
                Integer.class, sellerAuth.user().id()
        );
        assertEquals(40, balanceVendedor);
    }

    @Test
    void comprarExactamenteLaCantidadTotalDeTokensDisponibles() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Erling Haaland", 15);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 3);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 3))
                .retrieve()
                .toBodilessEntity();

        acreditarFondosAUsuario(buyerAuth.user().id(), 100);

        ResponseEntity<Map<String, Object>> response = restClient.post()
                .uri("/orders/buy")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearBuyRequest(player.getId(), 3))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Ya no deben quedar órdenes de venta abiertas para este jugador
        Integer ordenesAbiertas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sell_orders WHERE player_id = ? AND status = 'OPEN'",
                Integer.class, player.getId()
        );
        assertEquals(0, ordenesAbiertas);
    }

    @Test
    void comprarTokensSinAutenticacionDevuelveError() {
        Player player = crearJugador("Kylian Mbappe", 25);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.post()
                        .uri("/orders/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearBuyRequest(player.getId(), 1))
                        .retrieve()
                        .toBodilessEntity()
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    void comprarTokensConCantidadInvalidaDevuelveBadRequest() {
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);
        Player player = crearJugador("Pedri", 10);

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.post()
                        .uri("/orders/buy")
                        .header("Authorization", "Bearer " + buyerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearBuyRequest(player.getId(), 0))
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void comprarTokensSinLiquidezSuficienteDevuelveConflict() {
        // Flujo excepcional: no hay suficientes órdenes de venta abiertas en el mercado
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);
        Player player = crearJugador("Gavi", 10);
        acreditarFondosAUsuario(buyerAuth.user().id(), 500);

        // Se intenta comprar 5 tokens cuando no hay ninguna orden publicada
        assertThrows(HttpClientErrorException.Conflict.class, () ->
                restClient.post()
                        .uri("/orders/buy")
                        .header("Authorization", "Bearer " + buyerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearBuyRequest(player.getId(), 5))
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void comprarTokensConSaldoInsuficienteDevuelvePaymentRequired() {
        // Flujo excepcional: el comprador no tiene suficientes créditos para completar la orden
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Vinicius Jr", 50);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toBodilessEntity();

        // El comprador tiene 0 créditos
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.post()
                        .uri("/orders/buy")
                        .header("Authorization", "Bearer " + buyerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearBuyRequest(player.getId(), 2))
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, ex.getStatusCode());
    }

    // ============================================================================================
    // --------------------------------- Tests de POST /orders/sell --------------------------------
    // ============================================================================================

    @Test
    void publicarOrdenesDeVentaExitosamente() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Luka Modric", 30);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 4);

        ResponseEntity<List<SellOrderRecord>> response = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 3))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());

        for (SellOrderRecord dto : response.getBody()) {
            assertEquals(sellerAuth.user().id(), dto.userId());
            assertEquals(player.getId(), dto.playerId());
            assertEquals(30, dto.price());
            assertEquals(OrderStatus.OPEN, dto.status());
            assertNotNull(dto.id());
            assertNotNull(dto.tokenId());
        }
    }

    @Test
    void publicarVentaDeTodosLosTokensDisponibles() {
        // Caso borde: usuario publica la totalidad de sus tokens en venta
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Jude Bellingham", 40);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        ResponseEntity<List<SellOrderRecord>> response = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        // Al publicar todos en venta, ya no deben quedar tokens libres del usuario
        Integer tokensRestantes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tokens WHERE owner_id = ? AND player_id = ?",
                Integer.class, sellerAuth.user().id(), player.getId()
        );
        assertEquals(0, tokensRestantes);
    }

    @Test
    void publicarOrdenesDeVentaSinAutenticacionDevuelveError() {
        Player player = crearJugador("Rodri", 35);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.post()
                        .uri("/orders/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearSellRequest(player.getId(), 1))
                        .retrieve()
                        .toBodilessEntity()
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    void publicarOrdenesDeVentaConCantidadInvalidaDevuelveBadRequest() {
        // Flujo excepcional: cantidad <= 0
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Bukayo Saka", 25);

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restClient.post()
                        .uri("/orders/sell")
                        .header("Authorization", "Bearer " + sellerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearSellRequest(player.getId(), -1))
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void publicarOrdenesDeVentaConMasTokensDeLosQuePoseeDevuelveUnprocessableEntity() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Declan Rice", 20);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 1);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.post()
                        .uri("/orders/sell")
                        .header("Authorization", "Bearer " + sellerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearSellRequest(player.getId(), 5))
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(422, ex.getStatusCode().value());
    }

    @Test
    void publicarOrdenesDeVentaParaJugadorInexistenteDevuelveNotFound() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.post()
                        .uri("/orders/sell")
                        .header("Authorization", "Bearer " + sellerAuth.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(crearSellRequest(NON_EXISTENT_ID, 1))
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    // ============================================================================================
    // -------------------------------- Tests de POST /orders/{id}/cancel -------------------------
    // ============================================================================================

    @Test
    void cancelarOrdenDeVentaPropiaExitosamente() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Kevin De Bruyne", 45);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 1);

        ResponseEntity<List<SellOrderRecord>> sellResponse = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 1))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        Long orderId = sellResponse.getBody().getFirst().id();

        ResponseEntity<Void> cancelResponse = restClient.post()
                .uri("/orders/" + orderId + "/cancel")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());

        // La orden debe figurar como CANCELLED en base de datos
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM sell_orders WHERE id = ?",
                String.class, orderId
        );
        assertEquals(OrderStatus.CANCELLED.name(), status);

        // El token debe haber retornado al vendedor
        Integer tokensVendedor = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tokens WHERE owner_id = ? AND player_id = ?",
                Integer.class, sellerAuth.user().id(), player.getId()
        );
        assertEquals(1, tokensVendedor);
    }

    @Test
    void cancelarOrdenSinAutenticacionDevuelveError() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.post()
                        .uri("/orders/1/cancel")
                        .retrieve()
                        .toBodilessEntity()
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    void cancelarOrdenInexistenteDevuelveNotFound() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.post()
                        .uri("/orders/" + NON_EXISTENT_ID + "/cancel")
                        .header("Authorization", "Bearer " + sellerAuth.token())
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void cancelarOrdenDeOtroUsuarioDevuelveConflict() {
        // Flujo excepcional: un usuario intenta cancelar una orden que pertenece a otro usuario
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var otherAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Bernardo Silva", 30);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 1);

        ResponseEntity<List<SellOrderRecord>> sellResponse = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 1))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        Long orderId = sellResponse.getBody().getFirst().id();

        // El otro usuario intenta cancelarla
        assertThrows(HttpClientErrorException.Conflict.class, () ->
                restClient.post()
                        .uri("/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + otherAuth.token())
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    // ============================================================================================
    // --------------------------------- Tests de GET /orders/for-sell ----------------------------
    // ============================================================================================

    @Test
    void buscarOrdenesDeVentaAbiertasDevuelveResumenDeOrdenesDeOtrosUsuarios() {
        // Happy path: consulta de libro de órdenes de venta abiertas de otros usuarios
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Phil Foden", 25);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 3);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 3))
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sell")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals(player.getId(), response.getBody().content().getFirst().playerId());
        assertEquals("Phil Foden", response.getBody().content().getFirst().playerName());
        assertEquals(3, response.getBody().content().getFirst().quantity());
        assertEquals(25.0, response.getBody().content().getFirst().unitPrice());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasExcluyeOrdenesDelPropioUsuarioAutenticado() {
        // Caso borde: un vendedor no debe ver sus propias órdenes en el mercado de compra
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Lautaro Martinez", 22);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toBodilessEntity();

        // El propio vendedor consulta el endpoint for-sell
        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasSinOrdenesDevuelvePaginaVacia() {
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sell")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasConFiltroPorNombreDeJugadorDevuelveCoincidencias() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player p1 = crearJugador("Cristiano Ronaldo", 30);
        Player p2 = crearJugador("Lionel Messi", 35);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 2);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 2);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 2)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 2)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sell?nombre=Messi")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals("Lionel Messi", response.getBody().content().getFirst().playerName());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasConPaginacionPersonalizada() {
        // Caso borde: paginación específica
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player p1 = crearJugador("Player Alpha", 10);
        Player p2 = crearJugador("Player Beta", 12);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 1);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 1);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 1)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 1)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sell?page=0&size=1")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(2, response.getBody().totalElements());
        assertEquals(2, response.getBody().totalPages());
    }

    // ============================================================================================
    // ----------------------------- Tests de GET /orders/{id}/ordersSell -------------------------
    // ============================================================================================

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioDevuelveSusOrdenes() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var viewerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Antoine Griezmann", 18);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 4);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 4))
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/" + sellerAuth.user().id() + "/ordersSell")
                .header("Authorization", "Bearer " + viewerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals(player.getId(), response.getBody().content().getFirst().playerId());
        assertEquals("Antoine Griezmann", response.getBody().content().getFirst().playerName());
        assertEquals(4, response.getBody().content().getFirst().quantity());
        assertEquals(18.0, response.getBody().content().getFirst().unitPrice());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioSinOrdenesDevuelvePaginaVacia() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var viewerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/" + sellerAuth.user().id() + "/ordersSell")
                .header("Authorization", "Bearer " + viewerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioConFiltroPorNombre() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var viewerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player p1 = crearJugador("Federico Valverde", 28);
        Player p2 = crearJugador("Eduardo Camavinga", 24);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 2);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 2);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 2)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 2)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/" + sellerAuth.user().id() + "/ordersSell?nombre=Valverde")
                .header("Authorization", "Bearer " + viewerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals("Federico Valverde", response.getBody().content().getFirst().playerName());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioConPaginacionPersonalizada() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var viewerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player p1 = crearJugador("User Order Player 1", 10);
        Player p2 = crearJugador("User Order Player 2", 15);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 1);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 1);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 1)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 1)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/" + sellerAuth.user().id() + "/ordersSell?page=0&size=1")
                .header("Authorization", "Bearer " + viewerAuth.token())
                .retrieve()
                .toEntity(OrderSellSummaryPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(2, response.getBody().totalElements());
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioInexistenteDevuelveNotFound() {
        var viewerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                restClient.get()
                        .uri("/orders/" + NON_EXISTENT_ID + "/ordersSell")
                        .header("Authorization", "Bearer " + viewerAuth.token())
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioSinAutenticacionDevuelveError() {
        // Flujo excepcional: llamada sin token
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.get()
                        .uri("/orders/1/ordersSell")
                        .retrieve()
                        .toBodilessEntity()
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }
}
