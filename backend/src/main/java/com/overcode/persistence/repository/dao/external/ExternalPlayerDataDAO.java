package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerDataDAO {
    Optional<List<Team>> getDatosDeEquipos(List<TeamDraftDTO> teams);


}
