package com.overcode.testUtils.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;

public class TestMissionJPADTO extends DailyBonusMissionJPADTO {

    @Override
    public DailyBonusMission<?> aModelo() {
        return new TestDailyBonusMission();
    }

}
