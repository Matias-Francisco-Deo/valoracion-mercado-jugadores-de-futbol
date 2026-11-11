package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import com.overcode.service.exception.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TipoDailyBonusMission {;

    private static final List<MapMisionAJPADTO<? extends DailyBonusMissionJPADTO, ? extends DailyBonusMission<?>>> maps = new ArrayList<>(List.of(
            new MapBuyMissionAJPADTO(),
            new MapVisitPlayerMissionAJPADTO(),
            new MapVisit3PlayersMissionAJPADTO(),
            new MapBuyOrVisitMissionAJPADTO()
    ));

    public static DailyBonusMissionJPADTO getValueOf(DailyBonusMission<?> mision) {

        Optional<MapMisionAJPADTO<? extends DailyBonusMissionJPADTO, ? extends DailyBonusMission<?>>>
                mapMision = maps.stream().filter(
                map -> map.esAplicable(mision)
        ).findFirst();

        if (mapMision.isEmpty()) {
            throw new EntidadNoEncontradaException("Unexpected value: " + mision.getClass().getSimpleName());
        }

        return mapMision.get().mappear(mision);
    }

    public static void agregarMapMision(MapMisionAJPADTO<?, ?> misionADTO) {
        maps.add(misionADTO);
    }

    public static void eliminarMapMision(MapMisionAJPADTO<?, ?> misionADTO) {
        maps.remove(misionADTO);
    }
}
