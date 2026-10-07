package com.overcode.persistence.repository.interfaces;

import com.overcode.model.PlayerGameData;

import java.util.Optional;

public interface PlayerGameDataRepository {

    Optional<PlayerGameData> recuperar(Long id);
}
