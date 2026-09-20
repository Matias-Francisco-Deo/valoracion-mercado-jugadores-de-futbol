package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;

public interface ExternalPlayerDataDAO {
    Player getDatosDeJugador(PlayerDraftDTO playerDraftDTO);
    List<Player> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS);

}
