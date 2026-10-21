package com.overcode.model;

import com.overcode.model.exception.PosicionNoValidaException;
import com.overcode.model.player.PlayerPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerPositionTest {

    @Test
    void convierteCadaPosicionEnInglesAlModelo() {
        assertAll(
                () -> assertEquals(PlayerPosition.DEFENSOR, PlayerPosition.aModelo("Defender")),
                () -> assertEquals(PlayerPosition.ATACANTE, PlayerPosition.aModelo("Forward")),
                () -> assertEquals(PlayerPosition.CENTROCAMPISTA, PlayerPosition.aModelo("Midfielder")),
                () -> assertEquals(PlayerPosition.ARQUERO, PlayerPosition.aModelo("Goalkeeper"))
        );
    }

    @Test
    void conviertePosicionesSinImportarMayusculas() {
        assertEquals(PlayerPosition.ARQUERO, PlayerPosition.aModelo("goalkeeper"));
    }

    @Test
    void reconoceLaPosicionCuandoEsParteDeUnTextoMasLargo() {
        assertEquals(PlayerPosition.DEFENSOR, PlayerPosition.aModelo("Centre Defender"));
    }

    @Test
    void reconoceLaPosicionCuandoEsParteDeUnTextoMasLargoConParentesis() {
        assertEquals(PlayerPosition.ATACANTE, PlayerPosition.aModelo("Forward(Left)"));
    }


    @Test
    void rechazaTextoQueNoCorrespondeAUnaPosicion() {
        assertThrows(PosicionNoValidaException.class, () -> PlayerPosition.aModelo("Unknown"));
    }
}
