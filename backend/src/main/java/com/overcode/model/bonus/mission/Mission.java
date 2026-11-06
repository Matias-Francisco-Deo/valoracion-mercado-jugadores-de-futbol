package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class Mission<T> {
    private Long id;
    private String nombre;
    private boolean completed = false;

    public Mission(Long id, String nombre, boolean completed) {
        this.id = id;
        this.nombre = nombre;
        this.completed = completed;
    }

    public Mission(String nombre) {
        this(null, nombre, false);
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public abstract void complete(T context);

    public abstract Mission<T> clonar();

    public List<Mission<?>> getSubmissions() {
        return Collections.emptyList();
    }
}
