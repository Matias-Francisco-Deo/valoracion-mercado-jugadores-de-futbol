package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

//    @Test
//    void shouldRejectBlankUsername() {
//        assertThrows(IllegalArgumentException.class, () -> User("   ", "alice@example.com", "secret")); // TODO hacer en dto
//    }
//
//    @Test
//    void shouldRejectBlankPassword() {
//        assertThrows(IllegalArgumentException.class, () -> User("alice", "alice@example.com", "   ")); // TODO hacer en dto
//    }
}
