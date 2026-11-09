package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class AndDailyBonusMission<T> extends CompositeDailyBonusMission<T> {
    protected List<DailyBonusMission<?>> submissions = new ArrayList<>();

    protected AndDailyBonusMission(String nombre) {
        super(null, nombre, false, new ArrayList<>());
    }

    protected AndDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        super(null, nombre, false, submissions);
    }

    protected AndDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public void addSubmission(DailyBonusMission<?> dailyBonusMission) {
        this.submissions.add(dailyBonusMission);
    }

    @Override
    public void complete(T checkerObject) {
        setCompleted(submissions.stream().allMatch(DailyBonusMission::isCompleted));
    }

    @Override
    public List<DailyBonusMission<?>> getSubmissions() {
        return this.submissions;
    }
}
