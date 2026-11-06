package com.overcode.model.bonus.mission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyMission extends Mission<Void> {

    public BuyMission(Long id, String nombre, boolean completed) {
        super(id, nombre, completed);
    }

    public BuyMission(String nombre, boolean completed) {
        super(null, nombre, completed);
    }

    public BuyMission(String nombre) {
        super(nombre);
    }

    public BuyMission() {
        super("Comprar un token en el mercado");
    }

    @Override
    public void complete(Void unused) {
        setCompleted(true);
    }

    public void complete() {
        complete(null);
    }

    @Override
    public BuyMission clonar() {
        return new BuyMission(getId(), getNombre(), isCompleted());
    }
}
