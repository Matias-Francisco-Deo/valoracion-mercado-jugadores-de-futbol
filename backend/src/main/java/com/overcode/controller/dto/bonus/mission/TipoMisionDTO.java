package com.overcode.controller.dto.bonus.mission;

import com.overcode.model.bonus.mission.AndDailyBonusMission;
import com.overcode.model.bonus.mission.CompositeDailyBonusMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.OrDailyBonusMission;

public enum TipoMisionDTO {
    AND,
    OR,
    COMPOSITE,
    OBJECTIVE;

    public static TipoMisionDTO discriminarTipoMision(DailyBonusMission dailyBonusMission) {
        return switch (dailyBonusMission) {
            case AndDailyBonusMission ignored -> AND;
            case OrDailyBonusMission ignored -> OR;
            case CompositeDailyBonusMission ignored -> COMPOSITE;
            case DailyBonusMission ignored -> OBJECTIVE;
        };
    }

}
