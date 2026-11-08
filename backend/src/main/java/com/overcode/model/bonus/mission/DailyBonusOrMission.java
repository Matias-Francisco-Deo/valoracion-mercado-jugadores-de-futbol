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
public class DailyBonusOrMission extends OrMission<Void> {

    private BuyMission buyMission;
    private Visit3PlayersMission visit3PlayersMission;

    public DailyBonusOrMission(Long id, String nombre, boolean completed, List<Mission<?>> submissions) {
        super(id, nombre, completed, submissions);
        resolveBranches();
    }

    public DailyBonusOrMission(String nombre, List<Mission<?>> submissions) {
        super(nombre, submissions);
        resolveBranches();
    }

    public DailyBonusOrMission(BuyMission buyMission, Visit3PlayersMission visit3PlayersMission) {
        super("Misiones diarias del bonus", List.of(buyMission, visit3PlayersMission));
        this.buyMission = buyMission;
        this.visit3PlayersMission = visit3PlayersMission;
    }

    private void resolveBranches() {
        for (Mission<?> m : submissions) {
            if (m instanceof BuyMission bm) {
                this.buyMission = bm;
            } else if (m instanceof Visit3PlayersMission v3pm) {
                this.visit3PlayersMission = v3pm;
            }
        }
    }

    @Override
    public void complete(Void unused) {
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
    public DailyBonusOrMission clonar() {
        BuyMission clonedBuy = this.buyMission != null ? this.buyMission.clonar() : null;
        Visit3PlayersMission clonedVisits = this.visit3PlayersMission != null ? this.visit3PlayersMission.clonar() : null;
        if (clonedBuy != null && clonedVisits != null) {
            DailyBonusOrMission cloned = new DailyBonusOrMission(clonedBuy, clonedVisits);
            cloned.setId(null);
            cloned.setNombre(getNombre());
            cloned.setCompleted(isCompleted());
            return cloned;
        } else {
            List<Mission<?>> clonedSubs = new ArrayList<>();
            for (Mission<?> m : submissions) {
                clonedSubs.add(m.clonar());
            }
            return new DailyBonusOrMission(null, getNombre(), isCompleted(), clonedSubs);
        }
    }
}
