package com.overcode.model;

import com.overcode.model.user.User;
import org.junit.jupiter.api.Test;

import static com.overcode.testUtils.TestUserUtil.getUsuarioConCreditosIniciales;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    public static final User USER_1 = new User("alice", "alice@example.com", "secret");

    @Test
    void alCrearAlUsuarioSiSeIntentaDarUnBalanceNegativoDeCreditosTiene0() {
        User user = getUsuarioConCreditosIniciales("alice", -2d);
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
}
