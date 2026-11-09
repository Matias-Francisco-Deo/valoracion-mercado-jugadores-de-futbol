package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestDailyBonusMission<T> extends DailyBonusMission<T> {

    public TestDailyBonusMission(String nombre, boolean completed) {
        super(null, nombre, completed);
    }

    public TestDailyBonusMission(String nombre) {
        super(null, nombre, false);
    }

    public TestDailyBonusMission(Long id, String nombre, boolean completed) {
        super(id, nombre, completed);
    }

    @Override
    public void complete(T checkerObject) {
        setCompleted(true);
    }

    @Override
    public TestDailyBonusMission<T> clonar() {
        return new TestDailyBonusMission<>(getNombre(), isCompleted());
    }
}
