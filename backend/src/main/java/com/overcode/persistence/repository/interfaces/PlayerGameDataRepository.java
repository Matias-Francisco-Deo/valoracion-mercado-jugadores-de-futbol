package com.overcode.persistence.repository.interfaces;

import com.overcode.model.PlayerGameData;

import java.util.Optional;

public interface PlayerGameDataRepository {
    PlayerGameData guardar(PlayerGameData player);

    Optional<PlayerGameData> recuperar(Long id);
}
