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
public class BuyOrVisitMission extends OrDailyBonusMission<Void> {

    private BuyDailyBonusMission buyMission;
    private Visit3PlayersDailyBonusMission visit3PlayersMission; // TODO esto puede dar problemas, ojo

    public BuyOrVisitMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
    }

    public BuyOrVisitMission(BuyDailyBonusMission buyMission, Visit3PlayersDailyBonusMission visit3PlayersMission) {
        super("Visitar jugador o comprar token", List.of(buyMission, visit3PlayersMission));
        this.buyMission = buyMission;
        this.visit3PlayersMission = visit3PlayersMission;
    }

    @Override
    public void complete(Void checkerObject) {
        setCompleted(isCompleted());
    }

    public void completeBuy() {
        buyMission.complete();
        setCompleted(isCompleted());
    }

    public void completeVisit(Player player) {
        visit3PlayersMission.complete(player);
        setCompleted(isCompleted());

    }

    @Override
    public BuyOrVisitMission clonar() { // TODO revisar
        BuyDailyBonusMission clonedBuy = this.buyMission != null ? this.buyMission.clonar() : null;
        Visit3PlayersDailyBonusMission clonedVisits = this.visit3PlayersMission != null ? this.visit3PlayersMission.clonar() : null;
        if (clonedBuy != null && clonedVisits != null) {
            BuyOrVisitMission cloned = new BuyOrVisitMission(clonedBuy, clonedVisits);
            cloned.setId(null);
            cloned.setNombre(getNombre());
            cloned.setCompleted(isCompleted());
            return cloned;
        } else {
            List<DailyBonusMission<?>> clonedSubs = new ArrayList<>();
            for (DailyBonusMission<?> m : submissions) {
                clonedSubs.add(m.clonar());
            }
            return new BuyOrVisitMission(null, getNombre(), isCompleted(), clonedSubs);
        }
    }

//    public void actualizarConJugadores(List<Player> players) {
//        if (players.size() != 3) throw new MisionInvalidaException("La cantidad de jugadores debe ser 3");
//        visit3PlayersMission.actualizarConJugadores(players);
//    }
}
