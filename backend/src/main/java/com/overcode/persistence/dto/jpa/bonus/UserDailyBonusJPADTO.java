package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.dto.jpa.UserJPADTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "user_daily_bonus")
@Table(name = "user_daily_bonuses")
public class UserDailyBonusJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private UserJPADTO user;

    @Column(nullable = false)
    private Integer streakCount = 0;

    @Column()
    private LocalDate lastBonusClaimDate;

    @Column()
    private LocalDate activeMissionDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "root_mission_id")
    private DailyBonusMissionJPADTO activeMission;

    public UserDailyBonus aModelo(User user) {
        return new UserDailyBonus(
                this.id,
                user,
                this.streakCount,
                this.lastBonusClaimDate,
                this.activeMissionDate,
                this.activeMission != null ? this.activeMission.aModelo() : null
        );
    }

    public UserDailyBonus aModelo() {
        UserDailyBonus userDailyBonus = new UserDailyBonus(
//                this.id,
//                this.user.aModelo(),
//                this.streakCount,
//                this.lastBonusClaimDate,
//                this.activeMissionDate,
//                this.activeMission != null ? this.activeMission.aModelo() : null
        );
        userDailyBonus.setId(id);
        userDailyBonus.setUser(user.aModelo(userDailyBonus));
        userDailyBonus.setStreakCount(streakCount);
        userDailyBonus.setLastBonusClaimDate(lastBonusClaimDate);
        userDailyBonus.setActiveMissionDate(activeMissionDate);
        userDailyBonus.setActiveDailyBonusMission(activeMission != null ? activeMission.aModelo() : null);
        return userDailyBonus;
    }

    public static UserDailyBonusJPADTO desdeModelo(UserDailyBonus modelo) {
        if (modelo == null) {
            return null;
        }
        UserDailyBonusJPADTO dto = new UserDailyBonusJPADTO();
        dto.setId(modelo.getId());
        if (modelo.getUser() != null) {
            dto.setUser(UserJPADTO.desdeModelo(modelo.getUser(), dto));
        }
        dto.setStreakCount(modelo.getStreakCount());
        dto.setLastBonusClaimDate(modelo.getLastBonusClaimDate());
        dto.setActiveMissionDate(modelo.getActiveMissionDate());
        if (modelo.getActiveDailyBonusMission() != null) {
            dto.setActiveMission(DailyBonusMissionJPADTO.desdeModelo(modelo.getActiveDailyBonusMission()));
        }
        return dto;
    }

    public static UserDailyBonusJPADTO desdeModelo(UserDailyBonus modelo, UserJPADTO user) {
        if (modelo == null) {
            return null;
        }
        UserDailyBonusJPADTO dto = new UserDailyBonusJPADTO();
        dto.setId(modelo.getId());
        dto.setUser(user);
        dto.setStreakCount(modelo.getStreakCount());
        dto.setLastBonusClaimDate(modelo.getLastBonusClaimDate());
        dto.setActiveMissionDate(modelo.getActiveMissionDate());
        if (modelo.getActiveDailyBonusMission() != null) {
            dto.setActiveMission(DailyBonusMissionJPADTO.desdeModelo(modelo.getActiveDailyBonusMission()));
        }
        return dto;
    }
}
