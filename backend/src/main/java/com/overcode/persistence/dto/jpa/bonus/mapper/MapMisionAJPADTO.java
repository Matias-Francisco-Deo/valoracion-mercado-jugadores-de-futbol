package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.DailyBonusMissionJPADTO;


public abstract class MapMisionAJPADTO {
    private final Class<? extends DailyBonusMission<?>> misionClase;

    protected MapMisionAJPADTO(Class<? extends DailyBonusMission<?>> misionClase) {
        this.misionClase = misionClase;
    }

    public boolean esAplicable(DailyBonusMission<?> mision) {
        return this.misionClase == mision.getClass();
    }

    public abstract DailyBonusMissionJPADTO getDTO(DailyBonusMission<?> misionCotizacion);
}
