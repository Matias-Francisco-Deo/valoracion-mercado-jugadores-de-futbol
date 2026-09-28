package com.overcode.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    public static final User USER_1 = new User("alice", "alice@example.com", "secret");

    @Test
    void alCrearAlUsuarioSiSeIntentaDarUnBalanceNegativoDeCreditosTiene0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", -2, List.of());
        assertEquals(0, user.getCreditBalance());
    }

    @Test
    void alCrearAlUsuarioComienzaSinTokens() {
        User user = new User("alice", "alice@example.com", "secret");
        assertEquals(0, user.getTokens().size());
    }

    @Test
    void alAsignarUnBalanceDeCreditosNegativoConSetterSeSeteaA0() {
        User user = USER_1;
        user.setCreditBalance(-3);
        assertEquals(0, user.getCreditBalance());
    }
}
