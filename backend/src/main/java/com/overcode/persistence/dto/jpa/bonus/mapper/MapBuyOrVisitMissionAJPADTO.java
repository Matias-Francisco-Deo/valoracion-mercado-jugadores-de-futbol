package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.persistence.dto.jpa.bonus.mission.BuyOrVisitMissionJPADTO;
import org.jspecify.annotations.NonNull;


public class MapBuyOrVisitMissionAJPADTO extends MapMisionAJPADTO<BuyOrVisitMissionJPADTO, BuyOrVisitMission> {

    protected MapBuyOrVisitMissionAJPADTO() {
        super(BuyOrVisitMission.class);
    }

    @Override
    public @NonNull BuyOrVisitMissionJPADTO desdeModelo(BuyOrVisitMission misionCotizacion) {
        return new BuyOrVisitMissionJPADTO();
    }
}
