package com.overcode.persistence.dto.jpa.bonus.mission;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mapper.TipoDailyBonusMission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "mission")
@Table(name = "missions")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DailyBonusMissionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean completed;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_mission_id")
    private List<DailyBonusMissionJPADTO> submissions = new ArrayList<>();

    public abstract DailyBonusMission<?> aModelo();

    public static DailyBonusMissionJPADTO desdeModelo(DailyBonusMission<?> mision) {
        DailyBonusMissionJPADTO dto = TipoDailyBonusMission.getValueOf(mision);
        dto.setId(mision.getId());
        dto.setCompleted(mision.getCompleted());
        dto.setNombre(mision.getNombre());
        dto.setSubmissions(mision.getSubmissions().stream().map(DailyBonusMissionJPADTO::desdeModelo).toList());
        // ojo, nunca debería haber una misión que se llame a sí misma

        return dto;
    }

}
