package com.overcode.service.interfaces;

import com.overcode.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ExternalPlayerService {
    Optional<List<Player>> actualizarJugadores(Integer limit);
    CompletableFuture<Void> actualizarJugadoresAsync();
}
