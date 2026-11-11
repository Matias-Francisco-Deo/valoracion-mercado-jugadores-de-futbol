package com.overcode.testUtils.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name="test_mission")
@Table(name="test_missions")
public class TestMissionJPADTO extends DailyBonusMissionJPADTO {

    @Override
    public DailyBonusMission<?> aModelo() {
        return new TestDailyBonusMission();
    }

}
