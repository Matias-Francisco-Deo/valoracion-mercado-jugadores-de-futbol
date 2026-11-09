package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestDailyBonusMission<T> extends DailyBonusMission<T> {
    private Boolean completed = false;

    public TestDailyBonusMission(Long id, String nombre, boolean completed) {
        super(id, nombre);
        this.completed = completed;
    }

    public TestDailyBonusMission(String nombre, boolean completed) {
        super(null, nombre);
        this.completed = completed;
    }

    public TestDailyBonusMission(String nombre) {
        super(nombre);
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void complete(T checkerObject) {
        setCompleted(true);
    }

    @Override
    public TestDailyBonusMission<T> clonar() {
        return new TestDailyBonusMission<>(null, getNombre(), isCompleted());
    }
}
