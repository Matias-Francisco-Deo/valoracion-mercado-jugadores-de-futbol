package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.market.PeticionDeposito;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.service.interfaces.*;
import com.overcode.testUtils.TestService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.overcode.testUtils.TestLoginBonusUtil.getLoginBonusBase;
import static com.overcode.testUtils.TestPlayerUtil.getJugadorConRating;
import static com.overcode.testUtils.TestUserUtil.getUsuarioConCreditosIniciales;
import static junit.framework.TestCase.*;

@SpringBootTest
public class TokenMarketServiceTest {

    @Autowired
    private TokenMarketService tokenMarketService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerService playerService;

    @Value("${superuser.email}") String superuserEmail;

    private User user1;
    private User user2;
    Player player1;

    @Autowired
    private TestService testService;
    @Autowired
    private EstrategiaCotizacionService estrategiaCotizacionService;
    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;


    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarRegistros();
        testService.eliminarEstrategiasCotizacion();
        testService.eliminarLoginBonuses();

        userService.crearSuperusuario();
        configuracionMercadoService.crearConfiguracionSiNoExiste(new ConfiguracionMercado(1L, new EstrategiaCotizacionRating(), getLoginBonusBase()));

        user1 = getUsuarioConCreditosIniciales("pepito", 100.0);
        user2 = getUsuarioConCreditosIniciales("maria", 100.0);

        player1 = getJugadorConRating("messi", 10.0);

        user1 = userService.guardar(user1);
        user2 = userService.guardar(user2);

        player1 = playerService.guardar(player1);
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        testService.eliminarRegistros();
        testService.eliminarEstrategiasCotizacion();
        testService.eliminarLoginBonuses();


    }

    @Test
    void comprarTokenAlPrincipioTomaDelSuperuser() {
        tokenMarketService.buy(buyOrderParaUser1ConPlayer1());

        User user1Recuperado = userService.recuperar(user1.getId());
        Long playerTokenId = user1Recuperado.getTokens().getFirst().getPlayer().getId();
        assertEquals(playerTokenId, player1.getId());

    }


    @Test
    void comprarTokenAOtroUsuarioPermitePasarloDeManoEnMano() {

        tokenMarketService.buy(buyOrderParaUser1ConPlayer1());

        User user1Recuperado = userService.recuperar(user1.getId());
        Token tokenDeUser1 = user1Recuperado.getTokens().getFirst();

        tokenMarketService.sell(ordenDeVentaUser1DeTodosPlayer1(user1Recuperado, 1));

        tokenMarketService.buy(buyOrderParaUser2ConPlayer1());

        User user2Recuperado = userService.recuperar(user2.getId());
        user1Recuperado = userService.recuperar(user1.getId());

        assertTrue(user1Recuperado.getTokens().isEmpty());

        List<Token> tokensDeUser2 = user2Recuperado.getTokens();
        Long playerTokenId = tokensDeUser2.getFirst().getPlayer().getId();

        assertEquals(playerTokenId, player1.getId());
        assertTrue(tokensDeUser2.stream().anyMatch(
                token -> token.getId().equals(tokenDeUser1.getId())
                ));


    }

    @Test
    void comprarTodosLosTokensHaceQueElSuperuserNoTengaMas() {

        tokenMarketService.buy(ordenDeCompraDeUser1DeTodosPlayer1());

        User superuser = userService.recuperarPorEmail(superuserEmail).get();

        assertFalse(superuser.getTokens().stream().anyMatch(
                token -> token.getPlayer().getId().equals(player1.getId())
        ));

    }

    @Test
    void ponerTodosLosTokensEnVentaYVenderlosTodos() {

        tokenMarketService.buy(ordenDeCompraDeUser1DeTodosPlayer1());

        tokenMarketService.sell(ordenDeVentaUser1DeTodosPlayer1(user1, 100));

        tokenMarketService.buy(new OrdenDeCompra(user2, player1, 100));


        User user1Recuperado = userService.recuperar(user1.getId());
        User user2Recuperado = userService.recuperar(user2.getId());

        assertTrue(user1Recuperado.getTokens().isEmpty());
        assertEquals(100, user2Recuperado.getTokens().size());

    }

    private @NonNull PeticionVenta ordenDeVentaUser1DeTodosPlayer1(User user1, int quantity) {
        return new PeticionVenta(user1, player1, quantity);
    }

    @Test
    void comprarDisminuyeCreditos() {

        tokenMarketService.buy(ordenDeCompraDeUser1DeTodosPlayer1());

        tokenMarketService.sell(ordenDeVentaUser1DeTodosPlayer1(user1, 100));

        tokenMarketService.buy(new OrdenDeCompra(user2, player1, 100));

        User user2Recuperado = userService.recuperar(user2.getId());

        assertEquals(0, user2Recuperado.getCreditBalance(), 0.0001);

    }

    @Test
    void venderAumentaCreditos() {

        tokenMarketService.buy(ordenDeCompraDeUser1DeTodosPlayer1());

        tokenMarketService.sell(ordenDeVentaUser1DeTodosPlayer1(user1, 100));

        tokenMarketService.buy(new OrdenDeCompra(user2, player1, 100));

        User user1Recuperado = userService.recuperar(user1.getId());

        assertEquals(100, user1Recuperado.getCreditBalance(), 0.0001);

    }


    @Test
    void venderConSubidasDePrecioGeneraGanancia() {

        userService.depositarCreditos(new PeticionDeposito(user2, 10000d));
        userService.depositarCreditos(new PeticionDeposito(user2, 10000d));
        userService.depositarCreditos(new PeticionDeposito(user2, 10000d));
        userService.depositarCreditos(new PeticionDeposito(user2, 10000d));
        // tiene mucha plata 40100 creditos

        tokenMarketService.buy(ordenDeCompraDeUser1DeTodosPlayer1());

        tokenMarketService.sell(ordenDeVentaUser1DeTodosPlayer1(user1, 100));

        estrategiaCotizacionService.cotizarJugadores(); // ahora el jugador vale 1000

        tokenMarketService.buy(new OrdenDeCompra(user2, player1, 40));

        User user1Recuperado = userService.recuperar(user1.getId());

        assertTrue(user1Recuperado.getCreditBalance() > 100);

    }

    private @NonNull OrdenDeCompra ordenDeCompraDeUser1DeTodosPlayer1() {
        return new OrdenDeCompra(user1, player1, 100);
    }

    private @NonNull OrdenDeCompra buyOrderParaUser1ConPlayer1() {
        return new OrdenDeCompra(user1, player1, 1);
    }

    private @NonNull OrdenDeCompra buyOrderParaUser2ConPlayer1() {
        return new OrdenDeCompra(user2, player1, 100);
    }

}
