package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;

public interface ExternalPlayerDAOFootballDataAPI  {

    List<PlayerDraftDTO> listarJugadores();
}
