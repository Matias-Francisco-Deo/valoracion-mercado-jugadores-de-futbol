package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class OrMission<T> extends Mission<T> {
    protected List<Mission<?>> submissions = new ArrayList<>();

    public OrMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed);
        if (submissions != null) {
            this.submissions = new ArrayList<>(submissions);
        }
    }

    public OrMission(String nombre, List<Mission<?>> submissions) {
        this(null, nombre, false, submissions);
    }

    public OrMission(String nombre) {
        this(null, nombre, false, new ArrayList<>());
    }

    public void addSubmission(Mission<?> mission) {
        this.submissions.add(mission);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || (!submissions.isEmpty() && submissions.stream().anyMatch(Mission::isCompleted));
    }

    @Override
    public List<Mission<?>> getSubmissions() {
        return this.submissions;
    }
}
