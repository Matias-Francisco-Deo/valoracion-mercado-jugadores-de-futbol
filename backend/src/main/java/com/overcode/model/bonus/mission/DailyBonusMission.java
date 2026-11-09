package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class DailyBonusMission<T> {
    private Long id;
    private String nombre;
    private Boolean completed;

    protected DailyBonusMission(Long id, String nombre, Boolean completed) {
        this.id = id;
        this.nombre = nombre;
        this.completed = completed;
    }

    protected DailyBonusMission(String nombre) {
        this.nombre = nombre;
    }

    protected DailyBonusMission(String nombre, Boolean completed) {
        this(null, nombre, completed);
    }

    public boolean isCompleted() {return this.completed;}

    public abstract void complete(T checkerObject);

    public void setComplete() {
        setCompleted(true);
    }

    public abstract DailyBonusMission<T> clonar();

    public List<DailyBonusMission<?>> getSubmissions() {
        return Collections.emptyList();
    }
}
