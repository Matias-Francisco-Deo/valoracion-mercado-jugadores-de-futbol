package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisitPlayerMission extends Mission<Player> {
    private Player targetPlayer;

    public VisitPlayerMission(Long id, String nombre, boolean completed, Player targetPlayer) {
        super(id, nombre);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerMission(String nombre, Player targetPlayer) {
        super(nombre);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerMission(Player targetPlayer) {
        super("Visitar perfil de " + targetPlayer.getName());
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void complete(Player player) { // el jugador debe estar persistido para que ande correctamente (requiere id)
        if (isTargetPlayer(player)) {
            setCompleted(true);
        }
    }

    private boolean isTargetPlayer(Player player) {
        return this.targetPlayer.getId().equals(player.getId());
    }

    @Override
    public VisitPlayerMission clonar() {
        return new VisitPlayerMission(null, getNombre(), isCompleted(), this.targetPlayer);
    }
}
