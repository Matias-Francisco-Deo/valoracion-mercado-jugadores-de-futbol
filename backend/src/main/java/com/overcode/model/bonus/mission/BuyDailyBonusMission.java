package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyDailyBonusMission extends DailyBonusMission<Void> {

    public BuyDailyBonusMission(Long id, String nombre, boolean completed) {
        super(id, nombre);
    }

    public BuyDailyBonusMission(String nombre, boolean completed) {
        super(null, nombre);
    }

    public BuyDailyBonusMission(String nombre) {
        super(nombre);
    }

    public BuyDailyBonusMission() {
        super("Comprar un token en el mercado");
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
