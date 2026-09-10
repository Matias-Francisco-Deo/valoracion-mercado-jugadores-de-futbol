package com.overcode.service.interfaces;

import com.overcode.model.Player;

import java.util.List;

public interface PlayerService {

    Player crear(Player player);

    Player recuperar(Long id);

    List<Player> recuperarTodos();
}
