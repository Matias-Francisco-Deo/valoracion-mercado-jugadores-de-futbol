package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyDailyBonusMission extends DailyBonusMission<Void> {

    public BuyDailyBonusMission(Long id, String nombre, boolean completed) {
        super(id, nombre, completed);
    }

    public BuyDailyBonusMission() {
        super(null, "Comprar un token en el mercado", false);
    }

    @Override
    public void complete(Void checkerObject) {
        setCompleted(true);
    }

    public void complete() {
        complete(null);
    }

    @Override
    public BuyDailyBonusMission clonar() {
        return new BuyDailyBonusMission(null, getNombre(), isCompleted());
    }
}
