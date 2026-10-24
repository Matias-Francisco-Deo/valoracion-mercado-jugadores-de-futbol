package com.overcode.persistence.repository.interfaces;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    boolean existsByName(String name);

    boolean existeById(Long id);

    Player guardar(Player player);

    Optional<Player> recuperar(Long id);

    Page<Player> listarJugadores(PlayerFilter filtro, Pageable pageable);

    Page<Player> recuperarRankingDeJugadoresPorEstrategiaActiva(Pageable pageable);

    List<Player> listarTodos();

    List<Player> guardarTodos(List<Player> jugadores);
}
