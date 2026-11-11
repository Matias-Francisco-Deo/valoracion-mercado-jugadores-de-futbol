package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.BuyMissionJPADTO;
import org.jspecify.annotations.NonNull;


public class MapBuyMissionAJPADTO extends MapMisionAJPADTO<BuyMissionJPADTO, BuyDailyBonusMission> {

    protected MapBuyMissionAJPADTO() {
        super(BuyDailyBonusMission.class);
    }

    @Override
    public @NonNull BuyMissionJPADTO desdeModelo(BuyDailyBonusMission misionCotizacion) {
        return new BuyMissionJPADTO();
    }
}
