package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.LoginBonusRewards;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "login_bonus_rewards")
@Table(name = "login_bonus_rewards_data")
public class LoginBonusRewardsJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "rewards_list",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "rewards", nullable = false)
    private List<Double> rewards;


    public LoginBonusRewards aModelo() {
        return new LoginBonusRewards(this.rewards);
    }

    public static LoginBonusRewardsJPADTO desdeModelo(LoginBonusRewards loginBonusRewards) {
        if (loginBonusRewards == null) {
            return null;
        }
        LoginBonusRewardsJPADTO dto = new LoginBonusRewardsJPADTO();
        dto.setRewards(loginBonusRewards.getRewards());
        return dto;
    }
}
