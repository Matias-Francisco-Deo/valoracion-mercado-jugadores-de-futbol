package com.overcode.model.bonus;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class LoginBonusRewards extends DiaryBonusRewards {
    public LoginBonusRewards(List<Double> rewards) {
        super(rewards);
    }
}
