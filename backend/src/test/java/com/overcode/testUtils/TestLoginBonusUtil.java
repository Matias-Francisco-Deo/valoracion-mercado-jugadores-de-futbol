package com.overcode.testUtils;

import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.LoginBonus;

import java.util.EnumMap;
import java.util.Map;

public class TestLoginBonusUtil {

    public static LoginBonus getLoginBonusBase() {
        Map<DiaDeLaSemana, Double> rewards = new EnumMap<>(DiaDeLaSemana.class);
        rewards.put(DiaDeLaSemana.LUNES, 1.0);
        rewards.put(DiaDeLaSemana.MARTES, 2.0);
        rewards.put(DiaDeLaSemana.MIERCOLES, 2.0);
        rewards.put(DiaDeLaSemana.JUEVES, 5.0);
        rewards.put(DiaDeLaSemana.VIERNES, 5.0);
        rewards.put(DiaDeLaSemana.SABADO, 10.0);
        rewards.put(DiaDeLaSemana.DOMINGO, 15.0);
        return new LoginBonus("Bonus Base Semanal", rewards);
    }

    public static LoginBonus getLoginBonusPersonalizado(Map<DiaDeLaSemana, Double> rewards) {
        return new LoginBonus("Bonus Personalizado", rewards);
    }
}
