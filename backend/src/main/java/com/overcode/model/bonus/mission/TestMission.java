package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestMission<T> extends Mission<T> {
    // TODO dejarle el boolean a esta

    public TestMission(Long id, String nombre, boolean completed) {
        super(id, nombre, completed);
    }

    public TestMission(String nombre, boolean completed) {
        super(null, nombre, completed);
    }

    public TestMission(String nombre) {
        super(nombre);
    }

    @Override
    public void complete(T context) {
        setCompleted(true);
    }

    @Override
    public TestMission<T> clonar() {
        return new TestMission<>(null, getNombre(), isCompleted());
    }
}
