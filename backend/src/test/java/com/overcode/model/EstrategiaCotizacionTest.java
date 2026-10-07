package com.overcode.model;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.exception.EstrategiaInvalidaException;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionConValorConstante;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.*;

class EstrategiaCotizacionTest {
    private final EstrategiaCotizacion estrategiaCotizacion = new EstrategiaCotizacionSiempre2(0.0, 1.0);
    private final EstrategiaCotizacion estrategiaCotizacionSiempre5 = new EstrategiaCotizacionSiempre5(0.0, 1.0);
    public static Player JUGADOR_1 = getJugadorConNombre("Rich");

    @AfterEach
    void setUp() {
        JUGADOR_1 = getJugadorConNombre("Rich");
    }

    @Test
    void testEstrategiaCotizaAUnJugadorYSuValor() {

        List<Player> players = List.of(JUGADOR_1);

        estrategiaCotizacion.cotizar(players);

        assertEquals(2, players.getFirst().getCurrentPrice());

    }

    @Test
    void testEstrategiaCotizaAUnJugadorDosVecesYSuValorCambiaSegunEstrategia() {

        List<Player> players = List.of(JUGADOR_1);

        estrategiaCotizacion.cotizar(players);

        estrategiaCotizacionSiempre5.cotizar(players);

        assertEquals(5, players.getFirst().getCurrentPrice());

    }

    @Test
    void testEstrategiaTieneFactorEscalaQueMultiplicaElValorCotizado() {

        List<Player> players = List.of(JUGADOR_1);

        EstrategiaCotizacion estrategiaConFactorEscala = getEstrategiaCotizacionConFactorEscalaYValorConstante(2.0, 5.0);

        estrategiaConFactorEscala.cotizar(players);

        assertEquals(10, players.getFirst().getCurrentPrice());

    }

    @Test
    void testEstrategiaTieneValorBaseQueAumentaEnLaCantidadDadaFija() {

        List<Player> players = List.of(JUGADOR_1);

        EstrategiaCotizacion estrategiaConValorBase1 = getEstrategiaConValorBase(5.0);

        // como tal, el valor con el score da 0, se le suma el valor base (que es 5)

        estrategiaConValorBase1.cotizar(players);

        assertEquals(5, players.getFirst().getCurrentPrice());

    }

    @Test
    void testEstrategiaDebeTenerValorBaseMayorOIgualACero() {

        assertThrows(EstrategiaInvalidaException.class, () -> getEstrategiaConValorBase(-0.1));
        assertDoesNotThrow(() -> getEstrategiaConValorBase(0.0));
        assertDoesNotThrow(() -> getEstrategiaConValorBase(0.1));

    }

    @Test
    void testEstrategiaDebeTenerFactorEscalaMayorQueCero() {

        assertThrows(EstrategiaInvalidaException.class, () -> getEstrategiaCotizacionConFactorEscala(-0.1));
        assertThrows(EstrategiaInvalidaException.class, () -> getEstrategiaCotizacionConFactorEscala(0.0));
        assertDoesNotThrow(() -> getEstrategiaCotizacionConFactorEscala(0.1));

    }

    @Test
    void testEstrategiaDebeDar1SiElValorDeCotizacionEs0() {

        List<Player> players = List.of(JUGADOR_1);

        EstrategiaCotizacion estrategia = getEstrategiaCotizacionConFactorEscalaYValorConstante(1.0, -10D);

        estrategia.cotizar(players);

        assertEquals(1, players.getFirst().getCurrentPrice());

    }

    @Test
    void testEstrategiaDebeDar1SiElValorDeCotizacionEsMenorA1() {

        List<Player> players = List.of(JUGADOR_1);

        EstrategiaCotizacion estrategia = getEstrategiaCotizacionConFactorEscalaYValorConstante(1.0, 0D);

        estrategia.cotizar(players);

        assertEquals(1, players.getFirst().getCurrentPrice());

    }

    EstrategiaCotizacion getEstrategiaCotizacionConFactorEscalaYValorConstante(Double factorEscala, Double valorConstante) {
        return new EstrategiaCotizacionConValorConstante(valorConstante, factorEscala);
    }

    EstrategiaCotizacion getEstrategiaCotizacionConFactorEscala(Double factorEscala) {
        return new EstrategiaCotizacionConValorConstante(1.0, factorEscala);
    }

    EstrategiaCotizacion getEstrategiaConValorBase(Double valorBase) {
        return new EstrategiaCotizacionConValorConstante(valorBase);
    }

}
