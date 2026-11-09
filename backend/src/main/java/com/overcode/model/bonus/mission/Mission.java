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

    protected Mission(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    protected Mission(String nombre) {
        this(null, nombre);
    }

    public abstract boolean isCompleted();

    public abstract void complete(T context);

    public abstract Mission<T> clonar();

    public List<Mission<?>> getSubmissions() {
        return Collections.emptyList();
    }
}
