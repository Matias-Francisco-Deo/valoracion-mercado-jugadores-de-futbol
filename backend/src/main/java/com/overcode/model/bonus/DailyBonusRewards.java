package com.overcode.model.bonus;

import com.overcode.model.exception.bonus.DiaryBonusInvalidoException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class DailyBonusRewards {
    private final List<Double> rewards = new ArrayList<>();

    public DailyBonusRewards(List<Double> rewards) {
        if (rewards == null || rewards.size() != 7) {
            throw new DiaryBonusInvalidoException("Se deben agregar 7 recompensas, una por día de la semana.");
        }
        addAll(rewards);
    }

    public void add(Double reward) {
        if (reward == null || reward <= 0.0) {
            throw new DiaryBonusInvalidoException("La recompensa debe ser positiva");
        }
        if (rewards.size() >= 7) {
            throw new DiaryBonusInvalidoException("No se pueden agregar más recompensas, tope es 7 (uno por día de la semana)");
        }
        rewards.add(reward);
    }

    public void addAll(List<Double> rewards) {
        if (rewards == null) {
            throw new DiaryBonusInvalidoException("La lista de recompensas no puede ser nula.");
        }
        rewards.forEach(this::add);
    }

    public void remove(Double reward) {
        rewards.remove(reward);
    }

    public Double get(Integer index) {
        if (index == null || index >= 7 || index < 0) {
            throw new DiaryBonusInvalidoException("Índice fuera de rango. Solo se permiten índices de 0 a 6.");
        }
        return rewards.get(index);
    }
}
