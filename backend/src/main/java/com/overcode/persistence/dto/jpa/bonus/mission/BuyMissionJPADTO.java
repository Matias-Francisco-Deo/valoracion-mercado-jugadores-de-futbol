package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity(name = "buy_mission")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BuyMissionJPADTO extends DailyBonusMissionJPADTO {
    @Override
    public DailyBonusMission<?> aModelo() {
        return new BuyDailyBonusMission();
    }
}
