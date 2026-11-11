package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import jakarta.persistence.Entity;

@Entity(name = "buy_visit_mission")
public class BuyOrVisitMissionJPADTO extends DailyBonusMissionJPADTO {
    @Override
    public DailyBonusMission<?> aModelo() {
        return new BuyOrVisitMission();
    }
}
