package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerRepository {
    Optional<List<Player>> buscarYGuardarJugadores();
}
