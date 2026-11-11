package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import jakarta.persistence.Entity;


@Entity(name = "visit_3_players_mission")
public class Visit3PlayersMissionJPADTO extends DailyBonusMissionJPADTO {
    @Override
    public DailyBonusMission<?> aModelo() {
        return new Visit3PlayersDailyBonusMission();
    }
}
