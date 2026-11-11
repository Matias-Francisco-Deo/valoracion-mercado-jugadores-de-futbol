package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.DailyBonusMissionJPADTO;
import com.overcode.service.exception.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TipoDailyBonusMission {;

    private static final List<MapMisionAJPADTO> maps = new ArrayList<>(List.of(
            new MapBuyMissionAJPADTO()
    ));

    public static DailyBonusMissionJPADTO getValueOf(DailyBonusMission<?> mision) {

//        Class<? extends DailyBonusMission<?>> misionClase = mision.getClass();

        Optional<MapMisionAJPADTO> mapMision = maps.stream().filter(
                map -> map.esAplicable(mision)
        ).findFirst();

        if (mapMision.isEmpty()) {
            throw new EntidadNoEncontradaException("Unexpected value: " + mision.getClass().getSimpleName());
        }

        return mapMision.get().getDTO(mision);
    }

    public static void agregarMapMision(MapMisionAJPADTO misionADTO) {
        maps.add(misionADTO);
    }

    public static void eliminarMapMision(MapMisionAJPADTO misionADTO) {
        maps.remove(misionADTO);
    }
}
