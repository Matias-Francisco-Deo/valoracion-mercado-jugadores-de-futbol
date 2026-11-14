package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Entity(name = "buy_visit_mission")
public class BuyOrVisitMissionJPADTO extends DailyBonusMissionJPADTO {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private BuyMissionJPADTO buyMission;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Visit3PlayersMissionJPADTO visit3PlayersMission;

    @Override
    public DailyBonusMission<?> instanciarMision() {
        BuyOrVisitMission buyOrVisitMission = new BuyOrVisitMission();

        DailyBonusMission<?> modeloBuyMission = buyMission.aModelo();
        DailyBonusMission<?> modeloVisitMission = visit3PlayersMission.aModelo();

        buyOrVisitMission.setBuyMission((BuyDailyBonusMission) modeloBuyMission);
        buyOrVisitMission.setVisit3PlayersMission((Visit3PlayersDailyBonusMission) modeloVisitMission);
        buyOrVisitMission.setSubmissions(new ArrayList<>(List.of(modeloBuyMission, modeloVisitMission)));
        return buyOrVisitMission;
    }

    @Override
    public void setSubmissionsOnMission(DailyBonusMission<?> mission) {
        // se setean manualmente en este caso
    }

    @Override
    public void setDtoSubmissions(DailyBonusMission<?> mision) {
        // se setean manualmente en este caso
    }
}
