package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.PlayerDraftDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalPlayerDAOFootballDataAPIImpl implements ExternalPlayerDAOFootballDataAPI {
    @Override
    public List<PlayerDraftDTO> listarJugadores() {
        return List.of();
    }
}
