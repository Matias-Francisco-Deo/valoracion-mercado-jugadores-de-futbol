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
        super(id, nombre, completed);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerMission(String nombre, Player targetPlayer) {
        super(nombre);
        this.targetPlayer = targetPlayer;
    }

    public VisitPlayerMission(Player targetPlayer) {
        super(targetPlayer != null ? "Visitar perfil de " + targetPlayer.getName() : "Visitar perfil de jugador");
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void complete(Player player) {
        if (player != null && this.targetPlayer != null) {
            if (this.targetPlayer.getId() != null && this.targetPlayer.getId().equals(player.getId())) {
                setCompleted(true);
            } else if (this.targetPlayer.getName() != null && this.targetPlayer.getName().equals(player.getName())) {
                setCompleted(true);
            }
        }
    }

    @Override
    public VisitPlayerMission clonar() {
        return new VisitPlayerMission(getId(), getNombre(), isCompleted(), this.targetPlayer);
    }
}
