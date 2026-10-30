package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.exception.OperacionInvalidaOrdenException;
import com.overcode.model.exception.TokensInsuficientesParaVenderException;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.TokenMarketService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.overcode.testUtils.TestLoginBonusUtil.getLoginBonusBase;
import static com.overcode.testUtils.TestPlayerUtil.getJugadorConRating;
import static com.overcode.testUtils.TestUserUtil.getUsuarioConCreditosIniciales;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenMarketServiceSellTest {

    @Autowired
    private TokenMarketService tokenMarketService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;
    @Autowired
    private TestService testService;

    private User seller;
    private User buyer;
    private Player player;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        userService.crearSuperusuario();
        configuracionMercadoService.crearConfiguracionSiNoExiste(
                new ConfiguracionMercado(1L, new EstrategiaCotizacionRating(), getLoginBonusBase()));

        seller = getUsuarioConCreditosIniciales("seller", 1000d);
        buyer = getUsuarioConCreditosIniciales("buyer", 1000d);
        player = getJugadorConRating("Test Player", 10d);
        player.setCurrentPrice(10d);

        seller = userService.guardar(seller);
        buyer = userService.guardar(buyer);
        player = playerService.guardar(player);
        tokenMarketService.buy(new OrdenDeCompra(seller, player, 10));
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        testService.eliminarUsuarios();
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarRegistros();
        testService.eliminarEstrategiasCotizacion();
        testService.eliminarLoginBonuses();
    }

    private List<ResumenOrdenesDeVenta> openOrdersFor(Long userId) {
        return tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(
                userId, null, PageRequest.of(0, 20)).getContent();
    }

    // ------------------------------ createSellOrders ------------------------------

    @Test
    void createSellOrdersCreaUnaOrdenPorTokenYActualizaElResumen() {
        List<OrdenDeVenta> orders =
                tokenMarketService.sell(new PeticionVenta(seller, player, 6));

        assertEquals(6, orders.size());
        for (OrdenDeVenta order : orders) {
            assertEquals(OrderStatus.OPEN, order.getStatus());
            assertEquals(10, order.getPrice());
            assertEquals(seller.getId(), order.getOwner().getId());
            assertEquals(player.getId(), order.getPlayer().getId());
            assertNotNull(order.getToken().getId());
        }

        assertEquals(4, userService.recuperar(seller.getId()).getTokenCountForPlayer(player.getId()));
        List<ResumenOrdenesDeVenta> openOrders = openOrdersFor(seller.getId());
        assertEquals(1, openOrders.size());
        assertEquals(6, openOrders.getFirst().quantity());
        assertEquals(10, openOrders.getFirst().unitPrice());
    }

    @Test
    void createSellOrdersFallaSiElJugadorNoExiste() {
        Player missingPlayer = new Player(-1L);
        PeticionVenta post = new PeticionVenta(seller, missingPlayer, 5);

        assertThrows(EntidadNoEncontradaException.class,
                () -> tokenMarketService.sell(post));
    }

    @Test
    void createSellOrdersFallaSiElUsuarioNoExiste() {
        PeticionVenta post = new PeticionVenta(new User(-1L), player, 5);

        assertThrows(EntidadNoEncontradaException.class,
                () -> tokenMarketService.sell(post));
    }

    @Test
    void createSellOrdersFallaSiNoTieneTokensDeEseJugador() {
        PeticionVenta post = new PeticionVenta(buyer, player, 5);//el comprador sin tokens intenta vender
        assertThrows(TokensInsuficientesParaVenderException.class,
                () -> tokenMarketService.sell(post));

        assertTrue(openOrdersFor(buyer.getId()).isEmpty());
    }

    @Test
    void createSellOrdersFallaSiNoAlcanzaLaCantidadDeTokens() {
        PeticionVenta post = new PeticionVenta(seller, player, 11);
        assertThrows(TokensInsuficientesParaVenderException.class,
                () -> tokenMarketService.sell(post));

        assertEquals(10, userService.recuperar(seller.getId()).getTokenCountForPlayer(player.getId()));
        assertTrue(openOrdersFor(seller.getId()).isEmpty());
    }

    // ------------------------------ cancelOrder ------------------------------

    @Test
    @Disabled("Esta feature no está en funcionamiento todavía")
    void cancelOrderCancelaYDevuelveTokenAlVendedor() {
        OrdenDeVenta order = tokenMarketService.sell(new PeticionVenta(seller, player, 1)).getFirst();

        tokenMarketService.cancel(order.getId(), seller.getId());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(10, userService.recuperar(seller.getId()).getTokenCountForPlayer(player.getId()));
    }

    @Test
    void cancelOrderFallaSiLaOrdenNoExiste() {
        assertThrows(OrderNotFoundException.class, () -> tokenMarketService.cancel(-1L, seller.getId()));
    }

    @Test
    @Disabled("Esta feature no está en funcionamiento todavía")
    void cancelOrderFallaSiNoEsElDueño() {
        OrdenDeVenta order = tokenMarketService.sell(new PeticionVenta(seller, player, 1)).getFirst();

        assertThrows(OperacionInvalidaOrdenException.class,
                () -> tokenMarketService.cancel(order.getId(), buyer.getId()));
    }
}
