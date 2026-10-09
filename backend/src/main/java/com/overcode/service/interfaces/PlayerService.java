package com.overcode.service.interfaces;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlayerService {

    Player crear(Player player);

    Player recuperar(Long id);

    Page<Player> recuperarTodosConFiltro(PlayerFilter filter, Pageable pageable);
    List<Player> recuperarTodos();

    List<Player> listarTop5JugadoresPorRating();

    void guardarTodos(List<Player> jugadores);
}
