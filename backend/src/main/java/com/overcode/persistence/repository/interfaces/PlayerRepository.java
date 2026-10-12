package com.overcode.persistence.repository.interfaces;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    boolean existsByName(String name);

    Player guardar(Player player);

    Optional<Player> recuperar(Long id);

    Page<Player> listarJugadores(PlayerFilter filtro, Pageable pageable);

    List<Player> listarTop5JugadoresPorRating();

    List<Player> listarTodos();

    List<Player> guardarTodos(List<Player> jugadores);
}
