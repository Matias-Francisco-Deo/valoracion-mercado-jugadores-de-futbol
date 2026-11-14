package com.overcode.persistence.dto.jpa.bonus.mapper;

import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.dto.jpa.bonus.mission.VisitPlayerMissionJPADTO;
import org.jspecify.annotations.NonNull;


public class MapVisitPlayerMissionAJPADTO extends MapMisionAJPADTO<VisitPlayerMissionJPADTO, VisitPlayerDailyBonusMission> {

    protected MapVisitPlayerMissionAJPADTO() {
        super(VisitPlayerDailyBonusMission.class);
    }

    @Override
    public @NonNull VisitPlayerMissionJPADTO desdeModelo(VisitPlayerDailyBonusMission misionCotizacion) {
        VisitPlayerMissionJPADTO visitPlayerMissionJPADTO = new VisitPlayerMissionJPADTO();
        visitPlayerMissionJPADTO.setTargetPlayer(PlayerJPADTO.desdeModelo(misionCotizacion.getTargetPlayer()));
        return visitPlayerMissionJPADTO;
    }
}
