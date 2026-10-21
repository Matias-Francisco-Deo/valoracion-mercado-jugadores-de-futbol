package com.overcode.persistence.repository.interfaces;

import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerRepository {

    Optional<List<TeamDraftDTO>> listarEquiposDeJugadores(Integer maxPlayers);

    Optional<List<Team>> getDatosDeEquipos(List<TeamDraftDTO> teamDraftDTOS);

    boolean existsByExternalId(Player player);

    void updatePlayerByExternalId(Player player);
}
