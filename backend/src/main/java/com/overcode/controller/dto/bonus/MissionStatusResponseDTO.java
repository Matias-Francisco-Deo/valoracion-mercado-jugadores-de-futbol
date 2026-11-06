package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.mission.Mission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MissionStatusResponseDTO {
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

    public static MissionStatusResponseDTO desdeModelo(Mission<?> mission) {
        if (mission == null) {
            return null;
        }
        List<MissionStatusResponseDTO> subs = new ArrayList<>();
        if (mission.getSubmissions() != null) {
            for (Mission<?> child : mission.getSubmissions()) {
                subs.add(desdeModelo(child));
            }
        }
        return new MissionStatusResponseDTO(mission.getId(), mission.getNombre(), mission.isCompleted(), subs);
    }
}
