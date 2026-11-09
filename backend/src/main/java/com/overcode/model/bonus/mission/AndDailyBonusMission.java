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
