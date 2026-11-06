package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.DiaryBonus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "diary_bonus")
@Table(name = "diary_bonuses")
public class DiaryBonusJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rewards_id")
    private DiaryBonusRewardsJPADTO rewards;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "root_mission_id")
    private MissionJPADTO rootMission;

    public DiaryBonus aModelo() {
        return new DiaryBonus(
                this.id,
                this.nombre,
                this.rewards != null ? this.rewards.aModelo() : null,
                this.rootMission != null ? this.rootMission.aModelo() : null
        );
    }

    public static DiaryBonusJPADTO desdeModelo(DiaryBonus modelo) {
        if (modelo == null) {
            return null;
        }
        DiaryBonusJPADTO dto = new DiaryBonusJPADTO();
        dto.setId(modelo.getId());
        dto.setNombre(modelo.getNombre());
        if (modelo.getBonusRewards() != null) {
            dto.setRewards(DiaryBonusRewardsJPADTO.desdeModelo(modelo.getBonusRewards()));
        }
        if (modelo.getRootMission() != null) {
            dto.setRootMission(MissionJPADTO.desdeModelo(modelo.getRootMission()));
        }
        return dto;
    }
}
