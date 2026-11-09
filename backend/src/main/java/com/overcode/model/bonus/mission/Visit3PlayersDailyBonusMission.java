package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Visit3PlayersDailyBonusMission extends AndDailyBonusMission<Player> {

    public Visit3PlayersDailyBonusMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public Visit3PlayersDailyBonusMission(String nombre, List<DailyBonusMission<?>> submissions) {
        super(nombre, submissions);
    }

    public Visit3PlayersDailyBonusMission(List<VisitPlayerDailyBonusMission> missions) {
        super("Visitar 3 jugadores del día", new ArrayList<>(missions));
    }

    public Visit3PlayersDailyBonusMission(VisitPlayerDailyBonusMission m1, VisitPlayerDailyBonusMission m2, VisitPlayerDailyBonusMission m3) {
        super("Visitar 3 jugadores del día", List.of(m1, m2, m3));
    }

    @Override
    public void complete(Player checkerObject) {
        if (checkerObject != null) {
            for (DailyBonusMission<?> m : submissions) {
                if (m instanceof VisitPlayerDailyBonusMission vpm) {
                    vpm.complete(checkerObject);
                }
            }
        }
    }

    @Override
    public Visit3PlayersDailyBonusMission clonar() {
        List<DailyBonusMission<?>> clonedSubs = new ArrayList<>();
        for (DailyBonusMission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new Visit3PlayersDailyBonusMission(null, getNombre(), isCompleted(), clonedSubs);
    }
}
