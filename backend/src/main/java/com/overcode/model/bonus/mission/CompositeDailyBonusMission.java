package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class CompositeDailyBonusMission<T> extends DailyBonusMission<T>{
    protected List<DailyBonusMission<?>> submissions;

    protected CompositeDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed);
        this.submissions = new ArrayList<>(submissions);

    }
    protected CompositeDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        this(null, nombre, false, submissions);
    }

    protected CompositeDailyBonusMission(String nombre) {
        this(null, nombre, false, new ArrayList<>());
    }

    public void addSubmission(DailyBonusMission<?> dailyBonusMission) {
        this.submissions.add(dailyBonusMission);
    }

    @Override
    public List<DailyBonusMission<?>> getSubmissions() {
        return this.submissions;
    }

}
