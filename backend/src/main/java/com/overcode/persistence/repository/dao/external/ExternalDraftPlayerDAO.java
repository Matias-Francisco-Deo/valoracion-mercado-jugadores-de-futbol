package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.TeamDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalDraftPlayerDAO {

    Optional<List<TeamDraftDTO>> listarEquiposDeJugadores(Integer maxTeams);
}
