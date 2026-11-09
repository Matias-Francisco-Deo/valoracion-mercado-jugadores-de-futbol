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
public class BuyAndVisitMission extends OrDailyBonusMission<Void> {

    private BuyDailyBonusMission buyMission;
    private Visit3PlayersDailyBonusMission visit3PlayersMission;

    public BuyAndVisitMission(Long id, String nombre, boolean completed, List<DailyBonusMission<?>> submissions) {
        super(id, nombre, completed, submissions);
        resolveBranches();
    }

    public BuyAndVisitMission(BuyDailyBonusMission buyMission, Visit3PlayersDailyBonusMission visit3PlayersMission) {
        super("Visitar y comprar", List.of(buyMission, visit3PlayersMission));
        this.buyMission = buyMission;
        this.visit3PlayersMission = visit3PlayersMission;
    }

    private void resolveBranches() {
        for (DailyBonusMission<?> m : submissions) {
            if (m instanceof BuyDailyBonusMission bm) {
                this.buyMission = bm;
            } else if (m instanceof Visit3PlayersDailyBonusMission v3pm) {
                this.visit3PlayersMission = v3pm;
            }
        }
    }

    @Override
    public void complete(Void checkerObject) {
        if (buyMission != null) {
            buyMission.complete();
        }
    }

    public void completeVisit(Player player) {
        if (visit3PlayersMission != null) {
            visit3PlayersMission.complete(player);
        }
    }

    @Override
    public BuyAndVisitMission clonar() {
        BuyDailyBonusMission clonedBuy = this.buyMission != null ? this.buyMission.clonar() : null;
        Visit3PlayersDailyBonusMission clonedVisits = this.visit3PlayersMission != null ? this.visit3PlayersMission.clonar() : null;
        if (clonedBuy != null && clonedVisits != null) {
            BuyAndVisitMission cloned = new BuyAndVisitMission(clonedBuy, clonedVisits);
            cloned.setId(null);
            cloned.setNombre(getNombre());
            cloned.setCompleted(isCompleted());
            return cloned;
        } else {
            List<DailyBonusMission<?>> clonedSubs = new ArrayList<>();
            for (DailyBonusMission<?> m : submissions) {
                clonedSubs.add(m.clonar());
            }
            return new BuyAndVisitMission(null, getNombre(), isCompleted(), clonedSubs);
        }
    }
}
