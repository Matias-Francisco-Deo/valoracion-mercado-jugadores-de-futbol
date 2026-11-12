package com.overcode.model.bonus.mission;

import com.overcode.model.exception.bonus.mission.MisionInvalidaException;
import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Visit3PlayersDailyBonusMission extends AndDailyBonusMission<Player> {

    public Visit3PlayersDailyBonusMission(VisitPlayerDailyBonusMission m1, VisitPlayerDailyBonusMission m2, VisitPlayerDailyBonusMission m3) {
        super("Visitar 3 jugadores del día", List.of(m1, m2, m3));
    }

    public Visit3PlayersDailyBonusMission(List<Player> players) {
        super("Visitar 3 jugadores del día", crearSubmisiones(players));

    }

    private static @NonNull ArrayList<DailyBonusMission<?>> updateSubmissions(List<VisitPlayerDailyBonusMission> submissions) {
        return new ArrayList<>(submissions.stream().limit(3).toList());
    }

    private static @NonNull List<DailyBonusMission<?>> crearSubmisiones(List<Player> players) {
        if (players.size() != 3) throw new MisionInvalidaException("La cantidad de jugadores debe ser 3");
        return List.of(
                new VisitPlayerDailyBonusMission(players.get(0)),
                new VisitPlayerDailyBonusMission(players.get(1)),
                new VisitPlayerDailyBonusMission(players.get(2))
        );
    }

    @Override
    public void complete(Player checkerObject) {
        getSubmissions().forEach(m -> ((VisitPlayerDailyBonusMission) m).complete(checkerObject));
        setCompleted(isCompleted());
    }

    @Override
    public Visit3PlayersDailyBonusMission clonar() {
        List<VisitPlayerDailyBonusMission> clonedSubs = submissions.stream().map(m -> ((VisitPlayerDailyBonusMission) m).clonar()).toList();
        return new Visit3PlayersDailyBonusMission(clonedSubs.getFirst(), clonedSubs.get(1), clonedSubs.get(2));
    }

}
