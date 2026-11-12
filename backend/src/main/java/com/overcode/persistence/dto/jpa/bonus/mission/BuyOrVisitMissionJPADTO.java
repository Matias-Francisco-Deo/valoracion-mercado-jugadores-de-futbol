package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "buy_visit_mission")
public class BuyOrVisitMissionJPADTO extends DailyBonusMissionJPADTO {

    @OneToOne()
    private BuyMissionJPADTO buyMission;

    @OneToOne()
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

    public void setSubmissionsOnMission(DailyBonusMission<?> mission) {
        // se setean manualmente en este caso
    }


}
