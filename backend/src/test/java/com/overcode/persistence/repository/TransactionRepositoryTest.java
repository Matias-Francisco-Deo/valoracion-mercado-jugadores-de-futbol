package com.overcode.persistence.repository;

import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestService testService;

    private User usuarioGuardado;
    private Player jugadorGuardado;

    @BeforeEach
    void setUp() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();

        usuarioGuardado = userRepository.guardar(TestUserUtil.getUsuarioDeNombre("VendedorTest"));
        Player player = new Player();
        player.setName("JugadorVentas");
        player.setCurrentPrice(100.0);
        jugadorGuardado = playerRepository.guardar(player);
    }

    @AfterEach
    void tearDown() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void cuentaVentasEntreRango() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 5, 9, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 24, 23, 20, 20);

        LocalDateTime fecha1 = LocalDateTime.of(2026, 11, 9, 9, 0, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 11, 8, 23, 20, 20);

        guardarTransaccion(fecha1, TransactionType.SELL, 1, 50.0);
        guardarTransaccion(fecha2, TransactionType.SELL, 1, 50.0);

        Long totalVentas = transactionRepository.contarVentasEntre(inicio, fin);

        assertEquals(2L, totalVentas);
    }

    @Test
    void siNoHayTransacciones_laCantidadDeVentasEsCero() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 0, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 30, 23, 59, 59);

        Long totalVentas = transactionRepository.contarVentasEntre(inicio, fin);

        assertEquals(0L, totalVentas);
    }

    @Test
    void cuentaSoloTransaccionesDeTipoVentaDentroDelRangoDeFechas() {
        LocalDateTime inicioNoviembre = LocalDateTime.of(2026, 11, 1, 0, 0, 0);
        LocalDateTime finNoviembre = LocalDateTime.of(2026, 11, 30, 23, 59, 59);

        // Ventas dentro de noviembre (3 ventas)
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 14, 0, 0), TransactionType.SELL, 2, 100.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 15, 10, 30, 0), TransactionType.SELL, 1, 120.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 28, 18, 45, 0), TransactionType.SELL, 5, 95.0);

        // Compras dentro de noviembre (deben ser ignoradas, no son ventas)
        guardarTransaccion(LocalDateTime.of(2026, 11, 10, 12, 0, 0), TransactionType.BUY, 3, 100.0);

        // Ventas fuera de noviembre (una antes y una después, deben ser ignoradas)
        guardarTransaccion(LocalDateTime.of(2026, 10, 31, 23, 59, 58), TransactionType.SELL, 1, 80.0);
        guardarTransaccion(LocalDateTime.of(2026, 12, 1, 0, 0, 1), TransactionType.SELL, 1, 85.0);

        Long totalVentas = transactionRepository.contarVentasEntre(inicioNoviembre, finNoviembre);

        assertEquals(3L, totalVentas);
    }

    @Test
    void incluyeTransaccionesEnLosLimitesExactosDelRango() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 0, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 30, 23, 59, 59);

        guardarTransaccion(inicio, TransactionType.SELL, 1, 50.0);
        guardarTransaccion(fin, TransactionType.SELL, 1, 50.0);

        Long totalVentas = transactionRepository.contarVentasEntre(inicio, fin);

        assertEquals(2L, totalVentas);
    }

    private void guardarTransaccion(LocalDateTime fecha, TransactionType tipo, Integer cantidad, Double precioUnitario) {
        TransaccionFinanciera tx = new TransaccionFinanciera(null, usuarioGuardado, jugadorGuardado, fecha, tipo, cantidad, precioUnitario);
        transactionRepository.guardar(tx);
    }
}
