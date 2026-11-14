package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import org.jspecify.annotations.NonNull;


public abstract class MapMisionAJPADTO<D extends DailyBonusMissionJPADTO, M extends DailyBonusMission<?>> {
    private final Class<M> misionClase;

    protected MapMisionAJPADTO(Class<M> misionClase) {
        this.misionClase = misionClase;
    }

    public boolean esAplicable(DailyBonusMission<?> mision) {
        return this.misionClase == mision.getClass();
    }

    public D mappear(DailyBonusMission<?> mision) {
        return desdeModelo(misionClase.cast(mision));
    }

    public abstract @NonNull D desdeModelo(M misionCotizacion);
}
