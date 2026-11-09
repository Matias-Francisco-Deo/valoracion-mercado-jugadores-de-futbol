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
    protected List<DailyBonusMission<?>> submissions = new ArrayList<>();

    protected OrDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    protected OrDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        this(null, nombre, false, submissions);
    }

    protected OrDailyBonusMission(String nombre) {
        this(nombre, new ArrayList<>());
    }

    public void addSubmission(DailyBonusMission<?> dailyBonusMission) {
        this.submissions.add(dailyBonusMission);
    }

    @Override
    public boolean isCompleted() {
        return submissions.stream().anyMatch(DailyBonusMission::isCompleted);
    }

    @Override
    public List<DailyBonusMission<?>> getSubmissions() {
        return this.submissions;
    }
}
