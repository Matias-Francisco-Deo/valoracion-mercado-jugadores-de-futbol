package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void alCrearAlUsuarioSiSeIntentaDarUnBalanceNegativoDeCreditosTiene0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", -1, 0);
        assertEquals(0, user.getCreditBalance());
    }

    @Test
    void alCrearAlUsuarioSiSeIntentaDarUnBalanceNegativoDeTokensTiene0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", 0, -1);
        assertEquals(0, user.getTokens());
    }

    @Test
    void alAsignarUnBalanceDeCreditosNegativoConSetterSeSeteaA0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", 10, 5);
        user.setCreditBalance(-3);
        assertEquals(0, user.getCreditBalance());
    }

    @Test
    void alAsignarUnBalanceDeTokensNegativoConSetterSeSeteaA0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", 10, 5);
        user.setTokens(-7);
        assertEquals(0, user.getTokens());
    }
}
