package com.overcode.testUtils.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestDailyBonusMission extends DailyBonusMission<String> {

    public TestDailyBonusMission(String nombre, boolean completed) {
        super(null, nombre, completed);
    }

    public TestDailyBonusMission(String nombre) {
        super(null, nombre, false);
    }

    public TestDailyBonusMission(Long id, String nombre, boolean completed) {
        super(id, nombre, completed);
    }

    @Override
    public void complete(String checkerObject) {
        setComplete();
    }

    @Override
    public TestDailyBonusMission clonar() {
        return new TestDailyBonusMission(getNombre(), isCompleted());
    }
}
