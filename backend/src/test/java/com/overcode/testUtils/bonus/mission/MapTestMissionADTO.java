package com.overcode.testUtils.bonus.mission;

import com.overcode.persistence.dto.jpa.bonus.mapper.MapMisionAJPADTO;
import org.jspecify.annotations.NonNull;

public class MapTestMissionADTO extends MapMisionAJPADTO<TestMissionJPADTO, TestDailyBonusMission> {

    public MapTestMissionADTO() {
        super(TestDailyBonusMission.class);
    }

    @Override
    public @NonNull TestMissionJPADTO desdeModelo(TestDailyBonusMission misionCotizacion) {
        return new TestMissionJPADTO();
    }
}
