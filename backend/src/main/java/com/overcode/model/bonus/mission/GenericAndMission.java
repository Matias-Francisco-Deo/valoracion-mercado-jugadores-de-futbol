package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenericAndMission<T> extends AndMission<T> {

    public GenericAndMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public GenericAndMission(String nombre, List<Mission<?>> submissions) {
        super(nombre, submissions);
    }

    public GenericAndMission(String nombre) {
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
    public GenericAndMission<T> clonar() {
        List<Mission<?>> clonedSubs = new ArrayList<>();
        for (Mission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new GenericAndMission<>(getId(), getNombre(), isCompleted(), clonedSubs);
    }
}
