package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class AndMission<T> extends Mission<T> {
    protected List<Mission<?>> submissions = new ArrayList<>();

    public AndMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed);
        if (submissions != null) {
            this.submissions = new ArrayList<>(submissions);
        }
    }

    public AndMission(String nombre, List<Mission<?>> submissions) {
        this(null, nombre, false, submissions);
    }

    public AndMission(String nombre) {
        this(null, nombre, false, new ArrayList<>());
    }

    public void addSubmission(Mission<?> mission) {
        this.submissions.add(mission);
    }

    @Override
    public boolean isCompleted() {
        if (super.isCompleted()) {
            return true;
        }
        if (submissions.isEmpty()) {
            return false;
        }
        return submissions.stream().allMatch(Mission::isCompleted);
    }

    @Override
    public List<Mission<?>> getSubmissions() {
        return this.submissions;
    }
}
