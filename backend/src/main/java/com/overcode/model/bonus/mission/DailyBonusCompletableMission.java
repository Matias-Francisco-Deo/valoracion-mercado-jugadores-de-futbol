package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class DailyBonusCompletableMission<T> extends DailyBonusMission {

    protected DailyBonusCompletableMission(Long id, String nombre, Boolean completed) {
        super(id, nombre, completed);
    }

    protected DailyBonusCompletableMission(String nombre) {
        super(nombre);
    }

    protected DailyBonusCompletableMission(String nombre, Boolean completed) {
        super(nombre, completed);
    }

    public abstract void complete(T checkerObject);

    @Override
    public abstract DailyBonusCompletableMission<T> clonar();
}
