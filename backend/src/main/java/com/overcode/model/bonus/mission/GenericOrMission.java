package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenericOrMission<T> extends OrMission<T> {

    public GenericOrMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public GenericOrMission(String nombre, List<Mission<?>> submissions) {
        super(nombre, submissions);
    }

    public GenericOrMission(String nombre) {
        super(nombre);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void complete(T context) {
        for (Mission<?> sub : submissions) {
            try {
                ((Mission<T>) sub).complete(context);
            } catch (ClassCastException ignored) {
            }
        }
    }

    @Override
    public GenericOrMission<T> clonar() {
        List<Mission<?>> clonedSubs = new ArrayList<>();
        for (Mission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new GenericOrMission<>(null, getNombre(), isCompleted(), clonedSubs);
    }
}
