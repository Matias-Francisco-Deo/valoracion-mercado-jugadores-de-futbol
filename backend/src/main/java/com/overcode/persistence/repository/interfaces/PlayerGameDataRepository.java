package com.overcode.persistence.repository.interfaces;

import com.overcode.model.player.PlayerGameData;

import java.util.Optional;

public interface PlayerGameDataRepository {

    Optional<PlayerGameData> recuperar(Long id);
}
