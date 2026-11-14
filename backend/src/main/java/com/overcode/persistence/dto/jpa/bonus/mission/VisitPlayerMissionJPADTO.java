package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Setter;


@Entity(name = "visit_player_mission")
@Setter
public class VisitPlayerMissionJPADTO extends DailyBonusMissionJPADTO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private PlayerJPADTO targetPlayer;

    @Override
    public DailyBonusMission<?> instanciarMision() {
        return new VisitPlayerDailyBonusMission(targetPlayer.aModelo());
    }
}
