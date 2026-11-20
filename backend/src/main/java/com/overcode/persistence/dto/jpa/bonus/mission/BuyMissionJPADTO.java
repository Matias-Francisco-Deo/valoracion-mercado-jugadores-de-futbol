package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import jakarta.persistence.Entity;

@Entity(name = "buy_mission")
public class BuyMissionJPADTO extends DailyBonusMissionJPADTO {
    @Override
    public DailyBonusMission<?> instanciarMision() {
        return new BuyDailyBonusMission();
    }

}
