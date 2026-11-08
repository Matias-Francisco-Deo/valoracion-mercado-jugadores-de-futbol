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
public class Visit3PlayersMission extends AndMission<Player> {

    public Visit3PlayersMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public Visit3PlayersMission(String nombre, List<Mission<?>> submissions) {
        super(nombre, submissions);
    }

    public Visit3PlayersMission(List<VisitPlayerMission> missions) {
        super("Visitar 3 jugadores del día", new ArrayList<>(missions));
    }

    public Visit3PlayersMission(VisitPlayerMission m1, VisitPlayerMission m2, VisitPlayerMission m3) {
        super("Visitar 3 jugadores del día", List.of(m1, m2, m3));
    }

    @Override
    public void complete(Player player) {
        if (player != null) {
            for (Mission<?> m : submissions) {
                if (m instanceof VisitPlayerMission vpm) {
                    vpm.complete(player);
                }
            }
        }
    }

    @Override
    public Visit3PlayersMission clonar() {
        List<Mission<?>> clonedSubs = new ArrayList<>();
        for (Mission<?> m : submissions) {
            clonedSubs.add(m.clonar());
        }
        return new Visit3PlayersMission(null, getNombre(), isCompleted(), clonedSubs);
    }
}
