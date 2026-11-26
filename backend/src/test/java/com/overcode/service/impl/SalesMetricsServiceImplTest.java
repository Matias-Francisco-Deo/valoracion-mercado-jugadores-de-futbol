package com.overcode.service.impl;

import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.metricas.MonthlySalesPetition;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.SalesMetricsService;
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
class SalesMetricsServiceImplTest {

    @Autowired
    private SalesMetricsService salesMetricsService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestService testService;

    private User usuarioTest;
    private Player jugadorTest;

    @BeforeEach
    void setUp() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();

        usuarioTest = userRepository.guardar(TestUserUtil.getUsuarioDeNombre("VendedorServiceTest"));
        Player player = new Player();
        player.setName("JugadorServiceTest");
        player.setCurrentPrice(100.0);
        jugadorTest = playerRepository.guardar(player);
    }

    @AfterEach
    void tearDown() {
        testService.eliminarRegistros();
        testService.eliminarUsuarios();
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void obtenerCantidadVentasMensuales_conVentasEnElMes_retornaCantidadCorrecta() {
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 10, 0, 0), TransactionType.SELL, 1, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 15, 14, 30, 0), TransactionType.SELL, 2, 75.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 28, 20, 0, 0), TransactionType.SELL, 1, 100.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(3L, totalVentas);
    }

    @Test
    void obtenerCantidadVentasMensuales_sinTransacciones_retornaCero() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(0L, totalVentas);
    }

    @Test
    void obtenerCantidadVentasMensuales_ignoraCompras() {
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 10, 0, 0), TransactionType.SELL, 1, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 10, 11, 0, 0), TransactionType.BUY, 3, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 20, 18, 0, 0), TransactionType.BUY, 2, 80.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(1L, totalVentas);
    }

    @Test
    void obtenerCantidadVentasMensuales_ignoraVentasDeOtrosMeses() {
        // Venta en mes anterior (octubre)
        guardarTransaccion(LocalDateTime.of(2026, 10, 31, 23, 59, 58), TransactionType.SELL, 1, 60.0);
        // Ventas en mes consultado (noviembre)
        guardarTransaccion(LocalDateTime.of(2026, 11, 2, 10, 0, 0), TransactionType.SELL, 1, 70.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 25, 16, 0, 0), TransactionType.SELL, 1, 80.0);
        // Venta en mes posterior (diciembre)
        guardarTransaccion(LocalDateTime.of(2026, 12, 1, 0, 0, 1), TransactionType.SELL, 1, 90.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(2L, totalVentas);
    }

    @Test
    void obtenerCantidadVentasMensuales_incluyeTransaccionesEnLimitesExactosDelMes() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);

        guardarTransaccion(petition.getMinTime(), TransactionType.SELL, 1, 50.0);
        guardarTransaccion(petition.getMaxTime().withNano(0), TransactionType.SELL, 1, 50.0);

        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(2L, totalVentas);
    }

    @Test
    void obtenerCantidadVentasMensuales_enFebreroBisiesto_cuentaHasta29DeFebrero() {
        guardarTransaccion(LocalDateTime.of(2024, 2, 29, 12, 0, 0), TransactionType.SELL, 1, 100.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2024, 2);
        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        assertEquals(1L, totalVentas);
    }

    @Test
    void obtenerSumaComprasMensuales_conComprasEnElMes_retornaSumaCorrecta() {
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 10, 0, 0), TransactionType.BUY, 1, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 15, 14, 30, 0), TransactionType.BUY, 2, 75.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 28, 20, 0, 0), TransactionType.BUY, 1, 100.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(300.0, sumaCompras);
    }

    @Test
    void obtenerSumaComprasMensuales_sinTransacciones_retornaCero() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(0.0, sumaCompras);
    }

    @Test
    void obtenerSumaComprasMensuales_ignoraVentas() {
        guardarTransaccion(LocalDateTime.of(2026, 11, 5, 10, 0, 0), TransactionType.SELL, 1, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 10, 11, 0, 0), TransactionType.BUY, 3, 50.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 20, 18, 0, 0), TransactionType.BUY, 2, 80.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(310.0, sumaCompras);
    }

    @Test
    void obtenerSumaComprasMensuales_ignoraComprasDeOtrosMeses() {
        guardarTransaccion(LocalDateTime.of(2026, 10, 31, 23, 59, 58), TransactionType.BUY, 1, 60.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 2, 10, 0, 0), TransactionType.BUY, 1, 70.0);
        guardarTransaccion(LocalDateTime.of(2026, 11, 25, 16, 0, 0), TransactionType.BUY, 2, 80.0);
        guardarTransaccion(LocalDateTime.of(2026, 12, 1, 0, 0, 1), TransactionType.BUY, 1, 90.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);
        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(230.0, sumaCompras);
    }

    @Test
    void obtenerSumaComprasMensuales_incluyeTransaccionesEnLimitesExactosDelMes() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);

        guardarTransaccion(petition.getMinTime(), TransactionType.BUY, 1, 50.0);
        guardarTransaccion(petition.getMaxTime().withNano(0), TransactionType.BUY, 2, 25.0);

        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(100.0, sumaCompras);
    }

    @Test
    void obtenerSumaComprasMensuales_enFebreroBisiesto_sumaHasta29DeFebrero() {
        guardarTransaccion(LocalDateTime.of(2024, 2, 29, 12, 0, 0), TransactionType.BUY, 3, 100.0);

        MonthlySalesPetition petition = new MonthlySalesPetition(2024, 2);
        Double sumaCompras = salesMetricsService.obtenerSumaComprasMensuales(petition);

        assertEquals(300.0, sumaCompras);
    }

    private void guardarTransaccion(LocalDateTime fecha, TransactionType tipo, Integer cantidad, Double precioUnitario) {
        TransaccionFinanciera tx = new TransaccionFinanciera(null, usuarioTest, jugadorTest, fecha, tipo, cantidad, precioUnitario);
        transactionRepository.guardar(tx);
    }
}
