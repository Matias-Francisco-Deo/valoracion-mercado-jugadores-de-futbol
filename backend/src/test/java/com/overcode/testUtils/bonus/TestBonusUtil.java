package com.overcode.testUtils.bonus;

import com.overcode.controller.dto.bonus.mission.MissionStatusResponseDTO;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.player.Player;
import com.overcode.testUtils.TestDailyBonusUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TestBonusUtil {
    public static @NonNull ConfiguracionMercado getConfigConDailyOr(Player player, Player player2, Player player3) {
        Visit3PlayersDailyBonusMission visitMission = new Visit3PlayersDailyBonusMission(
                new VisitPlayerDailyBonusMission(player),
                new VisitPlayerDailyBonusMission(player2),
                new VisitPlayerDailyBonusMission(player3)
        );
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        BuyOrVisitMission dailyOr = new BuyOrVisitMission(buyMission, visitMission);

        return new ConfiguracionMercado(new EstrategiaCotizacionRendimientoTotal(), TestDailyBonusUtil.getDailyBonusBaseConMision(dailyOr));
    }

    public static int getObjetivosVistosEnMision(MissionStatusResponseDTO mision) {
        List<MissionStatusResponseDTO> misionesARecorrer = new ArrayList<>(mision.misiones());

        int objetivosVistos = misionesARecorrer.isEmpty() ? 1 : 0; // caso base
        while(!misionesARecorrer.isEmpty()) {
            MissionStatusResponseDTO misionActual = misionesARecorrer.getFirst();
            misionesARecorrer.removeFirst();

            objetivosVistos += misionActual.misiones().isEmpty() ? 1 : 0; // caso recursivo

            misionesARecorrer.addAll(misionActual.misiones());
        }
        return objetivosVistos;
    }

    public static int getMisionesTotalesEnArbol(MissionStatusResponseDTO misiones) {
        List<MissionStatusResponseDTO> misionesARecorrer = new ArrayList<>(misiones.misiones());

        int misionesVistas = 1; // caso base
        while(!misionesARecorrer.isEmpty()) {
            MissionStatusResponseDTO misionActual = misionesARecorrer.getFirst();
            misionesARecorrer.removeFirst();

            misionesVistas += 1; // caso recursivo

            misionesARecorrer.addAll(misionActual.misiones());
        }
        return misionesVistas;
    }
}
