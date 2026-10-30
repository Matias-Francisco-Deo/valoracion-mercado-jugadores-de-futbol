package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.exception.FondosInsuficientesException;
import com.overcode.model.exception.TokensInsuficientesParaComprarException;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.ResumenOrdenesDeVenta;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.TokenMarketService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.overcode.testUtils.TestLoginBonusUtil.getLoginBonusBase;
import static com.overcode.testUtils.TestPlayerUtil.getJugadorConRating;
import static com.overcode.testUtils.TestUserUtil.getUsuarioConCreditosIniciales;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TokenMarketServiceBuyTest {

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

    private User buyer;
    private User poorBuyer;
    private User sellerA;
    private User sellerB;
    private User selfBuyer;
    private Player player;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        userService.crearSuperusuario();
        configuracionMercadoService.crearConfiguracionSiNoExiste(
                new ConfiguracionMercado(1L, new EstrategiaCotizacionRating(), getLoginBonusBase()));

        buyer = getUsuarioConCreditosIniciales("buyer", 100d);
        poorBuyer = getUsuarioConCreditosIniciales("poor-buyer", 1d);
        sellerA = getUsuarioConCreditosIniciales("seller-a", 1000d);
        sellerB = getUsuarioConCreditosIniciales("seller-b", 100d);
        selfBuyer = getUsuarioConCreditosIniciales("self-buyer", 100d);
        player = getJugadorConRating("Test Player", 10d);
        player.setCurrentPrice(10d);

        buyer = userService.guardar(buyer);
        poorBuyer = userService.guardar(poorBuyer);
        sellerA = userService.guardar(sellerA);
        sellerB = userService.guardar(sellerB);
        selfBuyer = userService.guardar(selfBuyer);
        player = playerService.guardar(player);

        tokenMarketService.buy(new OrdenDeCompra(sellerA, player, 98));
        tokenMarketService.buy(new OrdenDeCompra(sellerB, player, 1));
        tokenMarketService.buy(new OrdenDeCompra(selfBuyer, player, 1));
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

    @Test
    void executeMarketBuyEjecutaCompraDeMultiplesOrdenesYGuardaTransacciones() {
        tokenMarketService.sell(new PeticionVenta(sellerA, player, 1));
        tokenMarketService.sell(new PeticionVenta(sellerB, player, 1));

        tokenMarketService.buy(new OrdenDeCompra(buyer, player, 2));

        assertEquals(80, userService.recuperar(buyer.getId()).getCreditBalance());
        assertEquals(30,
                userService.recuperar(sellerA.getId()).getCreditBalance());
        assertEquals(100,
                userService.recuperar(sellerB.getId()).getCreditBalance());
        assertEquals(2, userService.recuperar(buyer.getId()).getTokens().size());

        List<TransaccionFinanciera> buyerTransactions = userService.recuperarTransaccionesDeUnUser(
                buyer.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, buyerTransactions.size());
        TransaccionFinanciera buyerTransaction = buyerTransactions.getFirst();
        assertEquals(buyer.getId(), buyerTransaction.getOwner().getId());
        assertEquals(player.getId(), buyerTransaction.getPlayer().getId());
        assertEquals(2, buyerTransaction.getQuantity());
        assertEquals(10.0, buyerTransaction.getUnitPrice());
        assertEquals(20.0, buyerTransaction.getTotalPrice());

        List<TransaccionFinanciera> sellerATransactions = userService.recuperarTransaccionesDeUnUser(
                sellerA.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(2, sellerATransactions.size());
        TransaccionFinanciera sellerATransaction = sellerATransactions.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .findFirst()
                .orElseThrow();
        assertEquals(1, sellerATransaction.getQuantity());
        assertEquals(player.getId(), sellerATransaction.getPlayer().getId());

        List<TransaccionFinanciera> sellerBTransactions = userService.recuperarTransaccionesDeUnUser(
                sellerB.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(2, sellerBTransactions.size());
        TransaccionFinanciera sellerBTransaction = sellerBTransactions.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .findFirst()
                .orElseThrow();
        assertEquals(1, sellerBTransaction.getQuantity());
        assertEquals(player.getId(), sellerBTransaction.getPlayer().getId());
        assertTrue(openOrdersFor(sellerA.getId()).isEmpty());
        assertTrue(openOrdersFor(sellerB.getId()).isEmpty());
    }

    @Test
    void executeMarketBuyConUnSoloVendedorAgrupaTransaccionDeVenta() {
        tokenMarketService.sell(new PeticionVenta(sellerA, player, 3));

        tokenMarketService.buy(new OrdenDeCompra(buyer, player, 3));

        assertEquals(70, userService.recuperar(buyer.getId()).getCreditBalance());
        assertEquals(50,
                userService.recuperar(sellerA.getId()).getCreditBalance());

        List<TransaccionFinanciera> buyerTransactions = userService.recuperarTransaccionesDeUnUser(
                buyer.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, buyerTransactions.size());
        TransaccionFinanciera buyerTransaction = buyerTransactions.getFirst();
        assertEquals(TransactionType.BUY, buyerTransaction.getType());
        assertEquals(3, buyerTransaction.getQuantity());
        assertEquals(10.0, buyerTransaction.getUnitPrice());

        List<TransaccionFinanciera> sellerTransactions = userService.recuperarTransaccionesDeUnUser(
                sellerA.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(2, sellerTransactions.size());
        TransaccionFinanciera sellTransaction = sellerTransactions.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .findFirst()
                .orElseThrow();
        assertEquals(sellerA.getId(), sellTransaction.getOwner().getId());
        assertEquals(3, sellTransaction.getQuantity());
        assertEquals(player.getId(), sellTransaction.getPlayer().getId());
        assertTrue(openOrdersFor(sellerA.getId()).isEmpty());
    }

    @Test
    void executeMarketBuyFallaCuandoNoHaySuficienteLiquidez() {
        tokenMarketService.sell(new PeticionVenta(sellerA, player, 1));

        OrdenDeCompra order = new OrdenDeCompra(buyer, player, 5);
        assertThrows(TokensInsuficientesParaComprarException.class,
                () -> tokenMarketService.buy(order));

        List<ResumenOrdenesDeVenta> openOrders = openOrdersFor(sellerA.getId());
        assertEquals(1, openOrders.size());
        assertEquals(1, openOrders.getFirst().quantity());
        assertTrue(userService.recuperarTransaccionesDeUnUser(
                buyer.getId(), PageRequest.of(0, 10)).getContent().isEmpty());
        List<TransaccionFinanciera> sellerTransactions = userService.recuperarTransaccionesDeUnUser(
                sellerA.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, sellerTransactions.size());
        assertEquals(TransactionType.BUY, sellerTransactions.getFirst().getType());
    }

    @Test
    void executeMarketBuyFallaPorFondosInsuficientesDelComprador() {
        tokenMarketService.sell(new PeticionVenta(sellerA, player, 1));

        OrdenDeCompra order = new OrdenDeCompra(poorBuyer, player, 1);
        assertThrows(FondosInsuficientesException.class,
                () -> tokenMarketService.buy(order));

        List<ResumenOrdenesDeVenta> openOrders = openOrdersFor(sellerA.getId());
        assertEquals(1, openOrders.size());//sigue estando la orden que se intento comprar
        assertEquals(1, openOrders.getFirst().quantity());

        assertTrue(userService.recuperarTransaccionesDeUnUser(
                poorBuyer.getId(), PageRequest.of(0, 10)).getContent().isEmpty());//poorBuyer no tiene transacciones porque es "POBREEE"!!!
        List<TransaccionFinanciera> sellerTransactions = userService.recuperarTransaccionesDeUnUser(
                sellerA.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, sellerTransactions.size());
        assertEquals(TransactionType.BUY, sellerTransactions.getFirst().getType());
    }

    @Test
    void executeMarketBuyIgnoraLasOrdenesDelPropioComprador() {
        tokenMarketService.sell(new PeticionVenta(selfBuyer, player, 1));

        OrdenDeCompra order = new OrdenDeCompra(selfBuyer, player, 1);
        assertThrows(TokensInsuficientesParaComprarException.class,
                () -> tokenMarketService.buy(order));

        List<ResumenOrdenesDeVenta> openOrders = openOrdersFor(selfBuyer.getId());
        assertEquals(1, openOrders.size());
        assertEquals(1, openOrders.getFirst().quantity());
        List<TransaccionFinanciera> selfBuyerTransactions = userService.recuperarTransaccionesDeUnUser(
                selfBuyer.getId(), PageRequest.of(0, 10)).getContent();
        //solo debe existir la transacción de la compra del beforeEach
        assertEquals(1, selfBuyerTransactions.size());
        assertEquals(TransactionType.BUY, selfBuyerTransactions.getFirst().getType());
        assertEquals(1, selfBuyerTransactions.getFirst().getQuantity());
    }

    @Test
    void executeMarketBuyFallaSiElCompradorNoExiste() {
        User ghostBuyer = new User("ghost", "ghost@gmail.com", "pass");
        ghostBuyer.setId(-1L);
        OrdenDeCompra order = new OrdenDeCompra(ghostBuyer, player, 1);

        assertThrows(EntidadNoEncontradaException.class,
                () -> tokenMarketService.buy(order));
    }

    @Test
    void executeMarketBuyFallaSiElJugadorNoExiste() {
        Player missingPlayer = new Player(-1L);
        OrdenDeCompra order = new OrdenDeCompra(buyer, missingPlayer, 1);

        assertThrows(EntidadNoEncontradaException.class,
                () -> tokenMarketService.buy(order));
    }
}
