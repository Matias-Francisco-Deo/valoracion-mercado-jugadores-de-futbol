package com.overcode.controller.dto.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;

import java.util.List;

public record MissionStatusResponseDTO(
        Long id,
        String nombre,
        TipoMisionDTO tipoMisionDTO,
        boolean completed,
        List<MissionStatusResponseDTO> misiones
) {

    public static MissionStatusResponseDTO desdeModelo(DailyBonusMission<?> dailyBonusMission) {
        if (dailyBonusMission == null) {
            return null;
        }
        List<MissionStatusResponseDTO> misiones = dailyBonusMission.getSubmissions().stream().map(MissionStatusResponseDTO::desdeModelo).toList();


        return new MissionStatusResponseDTO(dailyBonusMission.getId(),
                dailyBonusMission.getNombre(),
                TipoMisionDTO.discriminarTipoMision(dailyBonusMission),
                dailyBonusMission.isCompleted(),
                misiones);
    }
}
