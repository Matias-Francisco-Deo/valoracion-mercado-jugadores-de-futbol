package com.overcode.service.interfaces;

import com.overcode.model.Player;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerService {
    Optional<List<Player>> buscarYGuardarJugadores(Integer limit);
}
