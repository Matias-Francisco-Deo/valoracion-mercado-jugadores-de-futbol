package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "diary_bonus")
@Table(name = "diary_bonuses")
public class DailyBonusJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rewards_id")
    private DailyBonusRewardsJPADTO rewards;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "root_mission_id")
    private DailyBonusMissionJPADTO rootMission;

    public DailyBonus aModelo() {
        return new DailyBonus(
                this.id,
                this.nombre,
                this.rewards != null ? this.rewards.aModelo() : null,
                this.rootMission != null ? this.rootMission.aModelo() : null
        );
    }

    public static DailyBonusJPADTO desdeModelo(DailyBonus modelo) {
        if (modelo == null) {
            return null;
        }
        DailyBonusJPADTO dto = new DailyBonusJPADTO();
        dto.setId(modelo.getId());
        dto.setNombre(modelo.getNombre());
        if (modelo.getBonusRewards() != null) {
            dto.setRewards(DailyBonusRewardsJPADTO.desdeModelo(modelo.getBonusRewards()));
        }
        if (modelo.getRootDailyBonusMission() != null) {
            dto.setRootMission(DailyBonusMissionJPADTO.desdeModelo(modelo.getRootDailyBonusMission()));
        }
        return dto;
    }
}
