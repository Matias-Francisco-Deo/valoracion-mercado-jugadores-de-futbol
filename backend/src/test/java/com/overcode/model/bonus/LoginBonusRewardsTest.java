package com.overcode.model.bonus;

import com.overcode.model.exception.bonus.LoginBonusInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginBonusRewardsTest {

    private LoginBonusRewards recompensas;

    @BeforeEach
    void setUp() {
        recompensas = new LoginBonusRewards();
    }

    @Test
    void alAgregarUnaRecompensaPositivaSePuedeConsultar() {
        recompensas.add(12.5);

        assertEquals(12.5, recompensas.get(0));
    }

    @Test
    void alAgregarUnaRecompensaIgualACeroLanzaExcepcion() {
        assertThrows(LoginBonusInvalidoException.class, () -> recompensas.add(0.0));
    }

    @Test
    void alAgregarUnaRecompensaNegativaLanzaExcepcion() {
        assertThrows(LoginBonusInvalidoException.class, () -> recompensas.add(-0.01));
    }

    @Test
    void permiteAgregarSieteRecompensas() {
        for (int dia = 0; dia < 7; dia++) {
            recompensas.add((double) dia + 1);
        }

        for (int dia = 0; dia < 7; dia++) {
            assertEquals((double) dia + 1, recompensas.get(dia));
        }
    }

    @Test
    void alAgregarUnaOctavaRecompensaLanzaExcepcionYSinModificarLasSieteAnteriores() {
        for (int dia = 0; dia < 7; dia++) {
            recompensas.add((double) dia + 1);
        }

        assertThrows(LoginBonusInvalidoException.class, () -> recompensas.add(8.0));

        for (int dia = 0; dia < 7; dia++) {
            assertEquals((double) dia + 1, recompensas.get(dia));
        }
    }

    @Test
    void permiteConsultarLosIndicesLimiteCeroYSeis() {
        for (int dia = 1; dia <= 7; dia++) {
            recompensas.add((double) dia);
        }

        assertEquals(1.0, recompensas.get(0));
        assertEquals(7.0, recompensas.get(6));
    }

    @Test
    void alConsultarUnIndiceMenorACeroLanzaExcepcion() {
        assertThrows(LoginBonusInvalidoException.class, () -> recompensas.get(-1));
    }

    @Test
    void alConsultarUnIndiceIgualASieteLanzaExcepcion() {
        assertThrows(LoginBonusInvalidoException.class, () -> recompensas.get(7));
    }

    @Test
    void alConsultarUnIndicePermitidoSinRecompensaLanzaExcepcionDeIndice() {
        assertThrows(IndexOutOfBoundsException.class, () -> recompensas.get(0));
    }

    @Test
    void alEliminarUnaRecompensaExistenteLasSiguientesSeDesplazan() {
        recompensas.add(1.0);
        recompensas.add(2.0);
        recompensas.add(3.0);

        recompensas.remove(2.0);

        assertEquals(1.0, recompensas.get(0));
        assertEquals(3.0, recompensas.get(1));
    }

    @Test
    void alEliminarUnaRecompensaInexistenteLasRecompensasNoCambian() {
        recompensas.add(1.0);

        recompensas.remove(2.0);

        assertEquals(1.0, recompensas.get(0));
    }
}
