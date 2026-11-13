package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.persistence.dto.jpa.bonus.mission.BuyMissionJPADTO;
import com.overcode.persistence.dto.jpa.bonus.mission.BuyOrVisitMissionJPADTO;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import com.overcode.persistence.dto.jpa.bonus.mission.Visit3PlayersMissionJPADTO;
import org.jspecify.annotations.NonNull;


public class MapBuyOrVisitMissionAJPADTO extends MapMisionAJPADTO<BuyOrVisitMissionJPADTO, BuyOrVisitMission> {

    protected MapBuyOrVisitMissionAJPADTO() {
        super(BuyOrVisitMission.class);
    }

    @Override
    public @NonNull BuyOrVisitMissionJPADTO desdeModelo(BuyOrVisitMission misionCotizacion) {
        BuyOrVisitMissionJPADTO buyOrVisitMissionJPADTO = new BuyOrVisitMissionJPADTO();

        BuyMissionJPADTO buyMission =
                (BuyMissionJPADTO) DailyBonusMissionJPADTO.desdeModelo(misionCotizacion.getBuyMission());
        Visit3PlayersMissionJPADTO visit3PlayersMission =
                (Visit3PlayersMissionJPADTO) DailyBonusMissionJPADTO.desdeModelo(misionCotizacion.getVisit3PlayersMission());

        buyOrVisitMissionJPADTO.setBuyMission(buyMission);
        buyOrVisitMissionJPADTO.setVisit3PlayersMission(visit3PlayersMission);
        buyOrVisitMissionJPADTO.setSubmissions(null);

        return buyOrVisitMissionJPADTO;
    }
}
