package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.DailyBonusRewards;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "diary_bonus_rewards")
@Table(name = "diary_bonus_rewards_data") // TODO cambiar nombres
public class DailyBonusRewardsJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "diary_rewards_list",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "rewards", nullable = false)
    private List<Double> rewards;

    public DailyBonusRewards aModelo() {
        return new DailyBonusRewards(this.rewards);
    }

    public static DailyBonusRewardsJPADTO desdeModelo(DailyBonusRewards rewards) {
        DailyBonusRewardsJPADTO dto = new DailyBonusRewardsJPADTO();
        dto.setRewards(rewards.getRewards());
        return dto;
    }
}
