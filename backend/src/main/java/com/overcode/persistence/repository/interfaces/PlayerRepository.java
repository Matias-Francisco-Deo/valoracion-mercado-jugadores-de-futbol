package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;

import java.util.Optional;

public interface PlayerRepository {

    Player guardar(Player player);

    Optional<Player> recuperar(Long id);
}
