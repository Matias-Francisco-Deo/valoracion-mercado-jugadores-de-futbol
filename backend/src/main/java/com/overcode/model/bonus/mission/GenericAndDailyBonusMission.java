package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenericAndDailyBonusMission<T> extends AndDailyBonusMission<T> { // y esta falopeada?? TODO

    public GenericAndDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public GenericAndDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        super(nombre, submissions);
    }

    public GenericAndDailyBonusMission(String nombre) {
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
    public GenericAndDailyBonusMission<T> clonar() {
        List<DailyBonusMission<?>> clonedSubs = new ArrayList<>();
        for (DailyBonusMission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new GenericAndDailyBonusMission<>(null, getNombre(), isCompleted(), clonedSubs);
    }
}
