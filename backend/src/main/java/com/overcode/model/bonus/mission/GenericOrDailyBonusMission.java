package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenericOrDailyBonusMission<T> extends OrDailyBonusMission<T> {

    public GenericOrDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public GenericOrDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        super(nombre, submissions);
    }

    public GenericOrDailyBonusMission(String nombre) {
        super(nombre);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void complete(T checkerObject) {
        for (DailyBonusMission<?> sub : submissions) {
            try {
                ((DailyBonusMission<T>) sub).complete(checkerObject);
            } catch (ClassCastException ignored) {
            }
        }
    }

    @Override
    public GenericOrDailyBonusMission<T> clonar() {
        List<DailyBonusMission<?>> clonedSubs = new ArrayList<>();
        for (DailyBonusMission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new GenericOrDailyBonusMission<>(null, getNombre(), isCompleted(), clonedSubs);
    }
}
