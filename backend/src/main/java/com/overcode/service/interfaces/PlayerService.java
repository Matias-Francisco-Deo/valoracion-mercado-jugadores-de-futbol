package com.overcode.service.interfaces;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;

import java.util.List;

public interface PlayerService {

    Player crear(Player player);

    Player recuperar(Long id);

    List<Player> recuperarTodosConFiltro(PlayerFilter filter);
    List<Player> recuperarTodosConFiltro();

    List<Player> actualizarDatosJugadores();

    List<Player> listarTop5JugadoresPorRating();
}
