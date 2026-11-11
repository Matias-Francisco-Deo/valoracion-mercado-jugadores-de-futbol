package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;


@Entity(name = "visit_3_players_mission")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Visit3PlayersMissionJPADTO extends DailyBonusMissionJPADTO {
    @Override
    public DailyBonusMission<?> aModelo() {
        return new Visit3PlayersDailyBonusMission();
    }
}
