package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MissionStatusResponseDTO { // TODO revisar este DTO
    private Long id;
    private String nombre;
    private boolean completed;
    private List<MissionStatusResponseDTO> subMisiones = new ArrayList<>();

    public MissionStatusResponseDTO(Long id, String nombre, boolean completed, List<MissionStatusResponseDTO> subMisiones) {
        this.id = id;
        this.nombre = nombre;
        this.completed = completed;
        if (subMisiones != null) {
            this.subMisiones = subMisiones;
        }
    }

    public static MissionStatusResponseDTO desdeModelo(DailyBonusMission<?> dailyBonusMission) {
        if (dailyBonusMission == null) {
            return null;
        }
        List<MissionStatusResponseDTO> subs = new ArrayList<>();
        if (dailyBonusMission.getSubmissions() != null) {
            for (DailyBonusMission<?> child : dailyBonusMission.getSubmissions()) {
                subs.add(desdeModelo(child));
            }
        }
        return new MissionStatusResponseDTO(dailyBonusMission.getId(), dailyBonusMission.getNombre(), dailyBonusMission.isCompleted(), subs);
    }
}
