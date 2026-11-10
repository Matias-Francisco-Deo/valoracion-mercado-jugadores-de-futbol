package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class OrDailyBonusMission<T> extends CompositeDailyBonusMission<T> {

    protected OrDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    protected OrDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        this(null, nombre, false, submissions);
    }

    protected OrDailyBonusMission(String nombre) {
        this(nombre, new ArrayList<>());
    }

    @Override
    public boolean isCompleted() {
        return this.getCompleted() || getSubmissions().stream().anyMatch(DailyBonusMission::isCompleted);
    }
}
