package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public BuyOrVisitMission clonar() {
        return new BuyOrVisitMission(this.buyMission.clonar(), this.visit3PlayersMission.clonar());
    }

}
