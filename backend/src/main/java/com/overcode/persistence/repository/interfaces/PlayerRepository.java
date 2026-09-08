package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    boolean existsByName(String name);

    Player guardar(Player player);

    Optional<Player> recuperar(Long id);

    List<Player> listarTodos();
}
