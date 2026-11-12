package com.overcode.service.interfaces;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.mission.DailyBonusMission;

public interface DailyBonusService {

    DailyBonus recuperarSeleccionado();

    DailyBonusMission<?> seleccionarNuevaMision();

    DailyBonusMission<?> crearNuevaMision();
}
