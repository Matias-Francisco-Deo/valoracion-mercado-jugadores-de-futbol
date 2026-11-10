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

    public Visit3PlayersDailyBonusMission(Long id, String nombre, boolean completed, List<VisitPlayerDailyBonusMission> submissions) {
        super(id, nombre, completed, new ArrayList<>(submissions.stream().limit(3).toList()));
    }

    public Visit3PlayersDailyBonusMission(VisitPlayerDailyBonusMission m1, VisitPlayerDailyBonusMission m2, VisitPlayerDailyBonusMission m3) {
        super("Visitar 3 jugadores del día", List.of(m1, m2, m3));
    }

    @Override
    public void complete(Player checkerObject) {
        getSubmissions().forEach(m -> ((VisitPlayerDailyBonusMission) m).complete(checkerObject));
        setCompleted(isCompleted());
    }

    @Override
    public Visit3PlayersDailyBonusMission clonar() {
        List<VisitPlayerDailyBonusMission> clonedSubs = submissions.stream().map(m -> ((VisitPlayerDailyBonusMission) m).clonar()).toList();
        return new Visit3PlayersDailyBonusMission(null, getNombre(), isCompleted(), clonedSubs);
    }
}
