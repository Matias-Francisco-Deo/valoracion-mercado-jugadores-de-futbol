package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import jakarta.persistence.*;
import lombok.Setter;


@Entity(name = "visit_player_mission")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Setter
public class VisitPlayerMissionJPADTO extends DailyBonusMissionJPADTO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private PlayerJPADTO targetPlayer;

    @Override
    public DailyBonusMission<?> aModelo() {
        return new VisitPlayerDailyBonusMission(targetPlayer.aModelo());
    }
}
