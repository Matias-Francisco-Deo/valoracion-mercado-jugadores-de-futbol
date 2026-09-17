package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;
import java.util.Optional;

public interface ExternalPlayerDAOFootballDataAPI  {

    Optional<List<PlayerDraftDTO>> listarJugadores();
}
