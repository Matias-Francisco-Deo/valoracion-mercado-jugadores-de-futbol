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
    private Boolean completed = false;
    private List<DailyBonusMission<?>> submissions = Collections.emptyList();
    // precondición: nunca debería haber una misión que se llame a sí misma en las submisiones

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

}
