package com.overcode.model.cotizacion;

import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EstrategiaCotizacionRatingTest {
    private final EstrategiaCotizacionRating estrategiaCotizacion = new EstrategiaCotizacionRating();

    @Test
    void estrategiaCotizaAJugadorNoDependeDeSuPosicion() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setRating(5.0);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        playerGameData.setPosition(PlayerPosition.ARQUERO);

        RegistroCotizacion score2 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertEquals(score1.getCotizacion(), score2.getCotizacion());

    }

    @Test
    void losValoresCotizadosSeCotizanPorEncimaDe100() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setRating(5.0);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() > 100);

    }

    @Test
    void jugadorMuyBuenoValeMasDe800() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setRating(9.0);

        playerGameData.setPosition(PlayerPosition.ATACANTE);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() > 800);

    }

    @Test
    void jugadorMuyMaloNoLlegaA200() {
        Player jugador1 = new Player();
        PlayerGameData playerGameData = new PlayerGameData();
        jugador1.setPlayerGameData(playerGameData);

        playerGameData.setRating(1.0);

        playerGameData.setPosition(PlayerPosition.DEFENSOR);

        RegistroCotizacion score1 = estrategiaCotizacion.cotizarAUnJugador(jugador1);

        assertTrue(score1.getCotizacion() < 200);

    }



}
