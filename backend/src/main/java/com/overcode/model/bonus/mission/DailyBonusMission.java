package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public <R> R reduce(R initialValue, Function<DailyBonusMission<?>, R> recursiveFunc, BinaryOperator<R> accFunc) {
        List<DailyBonusMission<?>> misionesARecorrer = new ArrayList<>();

        misionesARecorrer.add(this);

        DailyBonusMission<?> misionActual;

        R acc = initialValue; // caso base
        while(!misionesARecorrer.isEmpty()) {
            misionActual = misionesARecorrer.getFirst();
            misionesARecorrer.removeFirst();

            R acc2 = recursiveFunc.apply(misionActual); // caso recursivo
            acc = accFunc.apply(acc, acc2);

            misionesARecorrer.addAll(new ArrayList<>(misionActual.getSubmissions()));
        }
        return acc;
    }

    public void forEach(Consumer<DailyBonusMission<?>> action) {
        this.reduce(null, mission -> {action.accept(mission); return null;}, (n1, n2) -> n1);
    }

}
