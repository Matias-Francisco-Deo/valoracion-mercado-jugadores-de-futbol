package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.BuyDailyBonusMissionJPADTO;
import com.overcode.persistence.dto.jpa.bonus.DailyBonusMissionJPADTO;


public class MapBuyMissionAJPADTO extends MapMisionAJPADTO {

    protected MapBuyMissionAJPADTO() {
        super(BuyDailyBonusMission.class);
    }

    @Override
    public DailyBonusMissionJPADTO getDTO(DailyBonusMission<?> misionCotizacion) {
        return new BuyDailyBonusMissionJPADTO();
    }
}
