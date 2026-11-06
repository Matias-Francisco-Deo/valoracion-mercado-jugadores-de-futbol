package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.persistence.dto.jpa.UserJPADTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "user_diary_bonus")
@Table(name = "user_diary_bonuses")
public class UserDiaryBonusJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJPADTO user;

    @Column(name = "streak_count", nullable = false)
    private Integer streakCount = 0;

    @Column(name = "last_bonus_claim_date")
    private LocalDate lastBonusClaimDate;

    @Column(name = "active_mission_date")
    private LocalDate activeMissionDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "root_mission_id")
    private MissionJPADTO activeMission;

    public UserDiaryBonus aModelo() {
        return new UserDiaryBonus(
                this.id,
                this.user != null ? this.user.aModelo() : null,
                this.streakCount,
                this.lastBonusClaimDate,
                this.activeMissionDate,
                this.activeMission != null ? this.activeMission.aModelo() : null
        );
    }

    public static UserDiaryBonusJPADTO desdeModelo(UserDiaryBonus modelo) {
        if (modelo == null) {
            return null;
        }
        UserDiaryBonusJPADTO dto = new UserDiaryBonusJPADTO();
        dto.setId(modelo.getId());
        if (modelo.getUser() != null) {
            dto.setUser(UserJPADTO.desdeModelo(modelo.getUser()));
        }
        dto.setStreakCount(modelo.getStreakCount() != null ? modelo.getStreakCount() : 0);
        dto.setLastBonusClaimDate(modelo.getLastBonusClaimDate());
        dto.setActiveMissionDate(modelo.getActiveMissionDate());
        if (modelo.getActiveMission() != null) {
            dto.setActiveMission(MissionJPADTO.desdeModelo(modelo.getActiveMission()));
        }
        return dto;
    }
}
