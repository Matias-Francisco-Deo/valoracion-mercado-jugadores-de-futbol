package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.DiaryBonusRewards;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "diary_bonus_rewards")
@Table(name = "diary_bonus_rewards_data")
public class DiaryBonusRewardsJPADTO {

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

    public DiaryBonusRewards aModelo() {
        return new DiaryBonusRewards(this.rewards);
    }

    public static DiaryBonusRewardsJPADTO desdeModelo(DiaryBonusRewards rewards) {
        DiaryBonusRewardsJPADTO dto = new DiaryBonusRewardsJPADTO();
        dto.setRewards(rewards.getRewards());
        return dto;
    }
}
