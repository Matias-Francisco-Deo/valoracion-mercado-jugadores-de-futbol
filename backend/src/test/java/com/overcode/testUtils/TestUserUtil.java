package com.overcode.testUtils;

import com.overcode.model.user.User;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestUserUtil {

    public static User getUsuarioDeNombre(String nombre) {
        User user = new User(nombre, nombre.toLowerCase() + "@example.com", "Password123!", 0.0, new ArrayList<>());
        user.setStreakCount(0);
        user.setLastBonusClaimDate(null);
        return user;
    }

    public static User getUsuarioConRacha(String nombre, int racha, LocalDate ultimoReclamo) {
        User user = getUsuarioDeNombre(nombre);
        user.setStreakCount(racha);
        user.setLastBonusClaimDate(ultimoReclamo);
        return user;
    }
}
