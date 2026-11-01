package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.market.BuyTokenRequest;
import com.overcode.controller.dto.market.CancellTokenRequest;
import com.overcode.controller.dto.market.SellTokenRequest;
import com.overcode.controller.dto.venta.OrderSellSummaryPageResponseDTO;
import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.interfaces.TokenMarketService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
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
class TokenMarketE2eTest {

    private static final String SELLER_USERNAME = "orderSellerUser";
    private static final String SELLER_EMAIL = "orderseller@example.com";
    private static final String SELLER_PASSWORD = "Password123!";

    private static final String BUYER_USERNAME = "orderBuyerUser";
    private static final String BUYER_EMAIL = "orderbuyer@example.com";
    private static final String BUYER_PASSWORD = "Password123!";

    private static final Long NON_EXISTENT_ID = -1L;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenMarketService tokenMarketService;

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

    private void acreditarFondosAUsuario(Long userId, int creditos) {
        jdbcTemplate.update("UPDATE users SET credit_balance = ? WHERE id = ?", creditos, userId);

    }

    private BuyTokenRequest crearBuyRequest(Long playerId, Integer quantity) {
        return new BuyTokenRequest(playerId, quantity);
    }

    private void comprarTokens(String token, BuyTokenRequest body) {
        restClient.post()
                .uri("/orders/buy")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private void comprarTokensSinAutenticacion(BuyTokenRequest body) {
        restClient.post()
                .uri("/orders/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private void publicarOrdenesVenta(String token, SellTokenRequest body) {
        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private void publicarOrdenesVentaSinAutenticacion(SellTokenRequest body) {
        restClient.post()
                .uri("/orders/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private void cancelarOrdenSinAutenticacion(Long orderId) {
        restClient.post()
                .uri("/orders/" + orderId + "/cancel")
                .retrieve()
                .toBodilessEntity();
    }

    private void buscarOrdenesVenta(Long userId, String token) {
        restClient.get()
                .uri("/orders/" + userId + "/ordersSell")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    private void buscarOrdenesVentaSinAutenticacion(Long userId) {
        restClient.get()
                .uri("/orders/" + userId + "/ordersSell")
                .retrieve()
                .toBodilessEntity();
    }

    private SellTokenRequest crearSellRequest(Long playerId, Integer quantity) {
        return new SellTokenRequest(playerId, quantity);
    }

    // ============================================================================================
    // --------------------------------- Tests de POST /orders/buy ---------------------------------
    // ============================================================================================

    @Test
    void comprarTokensExitosamenteCuandoHayLiquidezYCreditosSuficientes() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Lionel Messi", 20d);
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
        ResponseEntity<Void> response = restClient.post()
                .uri("/orders/buy")
                .header("Authorization", "Bearer " + buyerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearBuyRequest(player.getId(), 2))
                .retrieve().toBodilessEntity();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Se verifica que el comprador ahora tiene 2 tokens en base de datos
        Integer tokensComprador = Math.toIntExact(userService.recuperar(buyerAuth.user().id()).getTokens().stream()
                .filter(t -> t.getPlayer().getId().equals(player.getId())).count());
        assertEquals(2, tokensComprador);

        // Se verifica que el vendedor recibió 2 * 20 = 40 créditos
        Double balanceVendedor = userService.recuperar(sellerAuth.user().id()).getCreditBalance();

        assertEquals(40, balanceVendedor);
    }

    @Test
    void comprarExactamenteLaCantidadTotalDeTokensDisponibles() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Erling Haaland", 15d);
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
        Integer ordenesAbiertas = tokenMarketService.buscarOrdersSellSummaryAbiertas(null, player.getName(), Pageable.unpaged()).getSize();

        assertEquals(0, ordenesAbiertas);
    }

    @Test
    void comprarTokensSinAutenticacionDevuelveError() {
        Player player = crearJugador("Kylian Mbappe", 25d);
        BuyTokenRequest body = crearBuyRequest(player.getId(), 1);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                comprarTokensSinAutenticacion(body)
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    void comprarTokensConCantidadInvalidaDevuelveBadRequest() {
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);
        Player player = crearJugador("Pedri", 10d);
        BuyTokenRequest body = crearBuyRequest(player.getId(), 0);
        String token = buyerAuth.token();

        assertThrows(HttpClientErrorException.BadRequest.class, () -> comprarTokens(token, body));
    }

    @Test
    void comprarTokensSinLiquidezSuficienteDevuelveConflict() {
        // Flujo excepcional: no hay suficientes órdenes de venta abiertas en el mercado
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);
        Player player = crearJugador("Gavi", 10d);
        acreditarFondosAUsuario(buyerAuth.user().id(), 500);

        // Se intenta comprar 5 tokens cuando no hay ninguna orden publicada
        BuyTokenRequest body = crearBuyRequest(player.getId(), 5);
        String token = buyerAuth.token();
        assertThrows(HttpClientErrorException.Conflict.class, () -> comprarTokens(token, body));
    }

    @Test
    void comprarTokensConSaldoInsuficienteDevuelvePaymentRequired() {
        // Flujo excepcional: el comprador no tiene suficientes créditos para completar la orden
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Vinicius Jr", 50d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toBodilessEntity();

        // El comprador tiene 0 créditos
        BuyTokenRequest body = crearBuyRequest(player.getId(), 2);
        String token = buyerAuth.token();
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                comprarTokens(token, body)
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, ex.getStatusCode());
    }

    // ============================================================================================
    // --------------------------------- Tests de POST /orders/sell --------------------------------
    // ============================================================================================

    @Test
    void publicarOrdenesDeVentaExitosamente() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Luka Modric", 30d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 4);

        ResponseEntity<List<SellOrderRecord>> response = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 3))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    void publicarVentaDeTodosLosTokensDisponibles() {
        // Caso borde: usuario publica la totalidad de sus tokens en venta
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Jude Bellingham", 40d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        ResponseEntity<List<SellOrderRecord>> response = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void publicarOrdenesDeVentaSinAutenticacionDevuelveError() {
        Player player = crearJugador("Rodri", 35d);
        SellTokenRequest body = crearSellRequest(player.getId(), 1);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                publicarOrdenesVentaSinAutenticacion(body)
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    void publicarOrdenesDeVentaConCantidadInvalidaDevuelveBadRequest() {
        // Flujo excepcional: amount <= 0
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Bukayo Saka", 25d);
        SellTokenRequest body = crearSellRequest(player.getId(), -1);
        String token = sellerAuth.token();

        assertThrows(HttpClientErrorException.BadRequest.class, () -> publicarOrdenesVenta(token, body));
    }

    @Test
    void publicarOrdenesDeVentaConMasTokensDeLosQuePoseeDevuelveBadRequest() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Declan Rice", 20d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 1);
        SellTokenRequest body = crearSellRequest(player.getId(), 5);
        String token = sellerAuth.token();

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                publicarOrdenesVenta(token, body)
        );

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void publicarOrdenesDeVentaParaJugadorInexistenteDevuelveNotFound() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        SellTokenRequest body = crearSellRequest(NON_EXISTENT_ID, 1);
        String token = sellerAuth.token();

        assertThrows(HttpClientErrorException.NotFound.class, () -> publicarOrdenesVenta(token, body));
    }

    // ============================================================================================
    // -------------------------------- Tests de POST /orders/cancel -------------------------
    // ============================================================================================

    @Test
    void cancelarOrdenDeVentaExitosamente() {
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        Player player = crearJugador("Kevin De Bruyne", 45d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 1);

        ResponseEntity<Void> sellResponse = restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 1))
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.CREATED, sellResponse.getStatusCode());

        var ordenesPublicadas = tokenMarketService.buscarOrdersSellSummaryAbiertas(null, player.getName(), Pageable.unpaged());
        assertEquals(1, ordenesPublicadas.getTotalElements());
        assertEquals(player.getId(), ordenesPublicadas.getContent().getFirst().playerId());
        assertEquals(1, ordenesPublicadas.getContent().getFirst().quantity());

        ResponseEntity<Void> cancelResponse = restClient.post()
                .uri("/orders/cancel")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CancellTokenRequest(player.getId(), 1))
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());

        // La orden ya no debe figurar entre las órdenes de venta abiertas
        var ordenesAbiertas = tokenMarketService.buscarOrdersSellSummaryAbiertas(null, player.getName(), Pageable.unpaged());
        assertEquals(0, ordenesAbiertas.getTotalElements());

        // El token debe haber retornado al vendedor
        Integer tokensVendedor = Math.toIntExact(userService.recuperar(sellerAuth.user().id()).getTokens().stream()
                .filter(t -> t.getPlayer().getId().equals(player.getId())).count());

        assertEquals(1, tokensVendedor);
    }

    @Test
    void cancelarOrdenSinAutenticacionDevuelveError() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                cancelarOrdenSinAutenticacion(1L)
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }


    // ============================================================================================
    // --------------------------------- Tests de GET /orders/forSell ----------------------------
    // ============================================================================================

    @Test
    void buscarOrdenesDeVentaAbiertasDevuelveResumenDeOrdenesDeOtrosUsuarios() {
        // Happy path: consulta de libro de órdenes de venta abiertas de otros usuarios
        var sellerAuth = registerUser(SELLER_USERNAME, SELLER_EMAIL, SELLER_PASSWORD);
        var buyerAuth = registerUser(BUYER_USERNAME, BUYER_EMAIL, BUYER_PASSWORD);

        Player player = crearJugador("Phil Foden", 25d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 3);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 3))
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/forSell")
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
        Player player = crearJugador("Lautaro Martinez", 22d);
        asignarTokensAUsuario(sellerAuth.user().id(), player.getId(), 2);

        restClient.post()
                .uri("/orders/sell")
                .header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON)
                .body(crearSellRequest(player.getId(), 2))
                .retrieve()
                .toBodilessEntity();

        // El propio vendedor consulta el endpoint forSell
        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/forSell")
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
                .uri("/orders/forSell")
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

        Player p1 = crearJugador("Cristiano Ronaldo", 30d);
        Player p2 = crearJugador("Lionel Messi", 35d);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 2);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 2);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 2)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 2)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/forSell?nombre=Messi")
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

        Player p1 = crearJugador("Player Alpha", 10d);
        Player p2 = crearJugador("Player Beta", 12d);
        asignarTokensAUsuario(sellerAuth.user().id(), p1.getId(), 1);
        asignarTokensAUsuario(sellerAuth.user().id(), p2.getId(), 1);

        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p1.getId(), 1)).retrieve().toBodilessEntity();
        restClient.post().uri("/orders/sell").header("Authorization", "Bearer " + sellerAuth.token())
                .contentType(MediaType.APPLICATION_JSON).body(crearSellRequest(p2.getId(), 1)).retrieve().toBodilessEntity();

        ResponseEntity<OrderSellSummaryPageResponseDTO> response = restClient.get()
                .uri("/orders/forSell?page=0&size=1")
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

        Player player = crearJugador("Antoine Griezmann", 18d);
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

        Player p1 = crearJugador("Federico Valverde", 28d);
        Player p2 = crearJugador("Eduardo Camavinga", 24d);
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

        Player p1 = crearJugador("User Order Player 1", 10d);
        Player p2 = crearJugador("User Order Player 2", 15d);
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
        Long userId = NON_EXISTENT_ID;
        String token = viewerAuth.token();

        assertThrows(HttpClientErrorException.NotFound.class, () -> buscarOrdenesVenta(userId, token));
    }

    @Test
    void buscarOrdenesDeVentaAbiertasDeUsuarioSinAutenticacionDevuelveError() {
        // Flujo excepcional: llamada sin token
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                buscarOrdenesVentaSinAutenticacion(1L)
        );

        assertTrue(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN);
    }
}
