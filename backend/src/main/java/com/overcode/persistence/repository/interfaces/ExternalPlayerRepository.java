package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerRepository {

    Optional<List<TeamDraftDTO>> listarEquiposDeJugadores(Integer maxPlayers);

    Optional<List<Team>> getDatosDeEquipos(List<TeamDraftDTO> teamDraftDTOS);

    Player updatePlayerByExternalId(Player player);
}
