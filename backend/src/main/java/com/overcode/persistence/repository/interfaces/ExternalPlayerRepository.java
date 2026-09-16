package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerRepository {
    List<Player> buscarYGuardarJugadores();
}
