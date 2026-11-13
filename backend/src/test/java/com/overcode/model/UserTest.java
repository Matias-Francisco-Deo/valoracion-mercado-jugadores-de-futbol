package com.overcode.model;

import com.overcode.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    public static final User USER_1 = new User("alice", "alice@example.com", "secret");

    @Test
    void alCrearAlUsuarioSiSeIntentaDarUnBalanceNegativoDeCreditosTiene0() {
        User user = new User(1L, "alice", "alice@example.com", "secret", -2d, List.of());
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
        user.setCreditBalance(-3d);
        assertEquals(0, user.getCreditBalance());
    }

    @Test
    void equalsAndHashCodeBasedOnId() {
        User userA = new User(1L);
        User userB = new User(1L);
        User userC = new User(2L);
        User userWithoutId = new User();

        assertEquals(userA, userA);
        assertEquals(userA, userB);
        assertEquals(userA.hashCode(), userB.hashCode());
        assertNotEquals(userA, userC);
        assertNotEquals(userA, userWithoutId);
        assertNotEquals(userA, null);
        assertNotEquals(userA, "string");
    }
}
