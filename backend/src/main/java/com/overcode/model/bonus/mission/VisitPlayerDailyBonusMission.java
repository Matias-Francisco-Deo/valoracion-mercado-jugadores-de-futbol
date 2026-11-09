package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisitPlayerDailyBonusMission extends DailyBonusMission<Player> {
    private Player targetPlayer;

    public VisitPlayerDailyBonusMission(Long id, String nombre, boolean completed, Player targetPlayer) {
        super(id, nombre, completed);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerDailyBonusMission(String nombre, Player targetPlayer) {
        super(nombre, false);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerDailyBonusMission(Player targetPlayer) {
        super("Visitar perfil de " + targetPlayer.getName());
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void complete(Player checkerObject) { // el jugador debe estar persistido para que ande correctamente (requiere id)
        if (isTargetPlayer(checkerObject)) {
            setCompleted(true);
        }
    }

    private boolean isTargetPlayer(Player player) {
        return this.targetPlayer.getId().equals(player.getId());
    }

    @Override
    public VisitPlayerDailyBonusMission clonar() {
        return new VisitPlayerDailyBonusMission(null, getNombre(), isCompleted(), this.targetPlayer);
    }
}
