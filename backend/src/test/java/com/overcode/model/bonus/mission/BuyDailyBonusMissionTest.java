package com.overcode.model.bonus.mission;

import com.overcode.testUtils.TestMissionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuyDailyBonusMissionTest {

    @Test
    void alCompletarBuyMissionCambiaSuEstadoACompletado() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        assertFalse(buyMission.isCompleted());

        buyMission.complete();
        assertTrue(buyMission.isCompleted());
    }

}
