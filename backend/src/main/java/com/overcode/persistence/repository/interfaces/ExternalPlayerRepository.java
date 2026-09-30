package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerRepository {

    Optional<List<PlayerDraftDTO>> listarJugadores(Integer maxPlayers);

    Optional<Player> getDatosDeJugador(PlayerDraftDTO playerDraftDTO);

    Player upsertPlayerByExternalId(Player player);
}
