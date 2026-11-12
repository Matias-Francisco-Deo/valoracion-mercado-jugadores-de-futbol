package com.overcode.service.interfaces;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.mission.BuyOrVisitMission;

public interface DailyBonusService {

    DailyBonus recuperarSeleccionado();

    BuyOrVisitMission seleccionarNuevaMisionVisitOrBuy();

    BuyOrVisitMission crearNuevaMisionBuyOrVisit();
}
