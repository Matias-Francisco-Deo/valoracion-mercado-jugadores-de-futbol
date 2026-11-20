package com.overcode.testUtils;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.user.User;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestUserUtil {

    public static User getUsuarioBase(String nombre) {
        return getUsuarioDeNombre(nombre);
    }

    public static User getUsuarioDeNombre(String nombre) {
        User user = new User(nombre, nombre.toLowerCase() + "@example.com", "Password123!", 0.0, new ArrayList<>());
        UserDailyBonus userDailyBonus = new UserDailyBonus(user);
        user.setUserDailyBonus(userDailyBonus);
        return user;
    }

    public static User getUsuarioConCreditosIniciales(String nombre, Double creditos) {
        User user = new User(nombre, nombre.toLowerCase() + "@example.com", "Password123!", creditos, new ArrayList<>());
        UserDailyBonus userDailyBonus = new UserDailyBonus(user);
        user.setUserDailyBonus(userDailyBonus);
        return user;
    }

    public static User getUsuarioConRacha(String nombre, int racha, LocalDate ultimoReclamo) {
        User user = getUsuarioDeNombre(nombre);
        user.getUserDailyBonus().setStreakCount(racha);
        user.getUserDailyBonus().setLastBonusClaimDate(ultimoReclamo);
        return user;
    }

    public static User getUsuarioConRachaYMision(String nombre, int racha, LocalDate ultimoReclamo, DailyBonusMission<?> dailyBonusMission) {
        User user = getUsuarioDeNombre(nombre);
        user.getUserDailyBonus().setStreakCount(racha);
        user.getUserDailyBonus().setLastBonusClaimDate(ultimoReclamo);
        user.getUserDailyBonus().setActiveMission(dailyBonusMission);
        return user;
    }
}
