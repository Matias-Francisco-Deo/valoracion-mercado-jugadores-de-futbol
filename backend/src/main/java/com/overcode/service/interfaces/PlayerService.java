package com.overcode.service.interfaces;

import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlayerService {

    Player crear(Player player);

    Player recuperar(Long id);

    Page<Player> recuperarTodosConFiltro(PlayerFilter filter, Pageable pageable);
    List<Player> recuperarTodos();

    Page<Player> recuperarRankingDeJugadoresPorEstrategiaActiva(Pageable pageable);
}
