package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.LoginBonusInvalidoException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class LoginBonusRewards {
    private final List<Double> rewards = new ArrayList<>();

    public LoginBonusRewards(List<Double> rewards) {
        if (rewards.size() != 7) {
            throw new LoginBonusInvalidoException("Se deben agregar 7 recompensas, una por día de la semana.");
        }
        addAll(rewards);
    }

    public void add(Double reward) {
        if (reward <= 0.0) throw new LoginBonusInvalidoException("La recompensa debe ser positiva");
        if (rewards.size() >= 7) throw new LoginBonusInvalidoException("No se pueden agregar más recompensas, tope es 7 (uno por día de la semana)");
        rewards.add(reward);
    }

    public void addAll(List<Double> rewards) {
        rewards.forEach(this::add);
    }

    public void remove(Double reward) {
        rewards.remove(reward);
    }

    public Double get(Integer index) {
        if (index >= 7 || index < 0) throw new LoginBonusInvalidoException("Índice fuera de rango. Solo se permiten índices de 0 a 6.");
        return rewards.get(index);
    }


}
