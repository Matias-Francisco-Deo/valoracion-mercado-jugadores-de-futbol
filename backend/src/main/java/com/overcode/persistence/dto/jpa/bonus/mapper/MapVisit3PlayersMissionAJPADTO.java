package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.Visit3PlayersMissionJPADTO;
import org.jspecify.annotations.NonNull;


public class MapVisit3PlayersMissionAJPADTO extends MapMisionAJPADTO<Visit3PlayersMissionJPADTO, Visit3PlayersDailyBonusMission> {

    protected MapVisit3PlayersMissionAJPADTO() {
        super(Visit3PlayersDailyBonusMission.class);
    }

    @Override
    public @NonNull Visit3PlayersMissionJPADTO desdeModelo(Visit3PlayersDailyBonusMission misionCotizacion) {
        return new Visit3PlayersMissionJPADTO();
    }
}
