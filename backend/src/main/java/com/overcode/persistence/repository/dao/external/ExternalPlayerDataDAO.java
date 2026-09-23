package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerDataDAO {
    Optional<Player> getDatosDeJugador(PlayerDraftDTO playerDraftDTO);
    List<Player> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS);

}
