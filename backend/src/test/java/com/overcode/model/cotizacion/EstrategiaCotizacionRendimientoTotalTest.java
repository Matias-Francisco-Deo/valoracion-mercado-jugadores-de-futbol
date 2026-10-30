package com.overcode.model.cotizacion;

import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EstrategiaCotizacionRendimientoTotalTest {
    private final EstrategiaCotizacionRendimientoTotal estrategiaCotizacion = new EstrategiaCotizacionRendimientoTotal();

    @Test
    void estrategiaCotizaAJugadorDependiendoDeSuPosicion() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setGoals(5);
        playerGameData.setShotsOnTarget(20); // datos relevantes en atacante
        playerGameData.setSuccessfulDribbles(30);

        playerGameData.setRating(5.0); // datos generales
        playerGameData.setKeyPasses(20);
        playerGameData.setAssists(40);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        playerGameData.setPosition(PlayerPosition.ARQUERO);

        RegistroCotizacion score2 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertNotEquals(score1.getCotizacion(), score2.getCotizacion());

    }

    @Test
    void losValoresCotizadosSeCotizanPorEncimaDe100() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setGoals(5);
        playerGameData.setShotsOnTarget(20); // datos relevantes en atacante
        playerGameData.setSuccessfulDribbles(30);

        playerGameData.setRating(5.0); // datos generales
        playerGameData.setKeyPasses(20);
        playerGameData.setAssists(40);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() > 100);

    }

    @Test
    void jugadorMuyBuenoValeMasDe800() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setGoals(15);
        playerGameData.setShotsOnTarget(30); // datos relevantes en atacante
        playerGameData.setSuccessfulDribbles(30);

        playerGameData.setRating(9.0); // datos generales
        playerGameData.setKeyPasses(20);
        playerGameData.setAssists(30);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() > 800);

    }

    @Test
    void jugadorMuyMaloNoLlegaA200() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setTackles(2);

        playerGameData.setRating(1.0); // datos generales
        playerGameData.setKeyPasses(1);
        playerGameData.setAssists(1);

        playerGameData.setPosition(PlayerPosition.DEFENSOR);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() < 200);

    }



}
