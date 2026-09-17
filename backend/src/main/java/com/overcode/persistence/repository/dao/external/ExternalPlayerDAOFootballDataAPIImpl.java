package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.PlayerDraftDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerDAOFootballDataAPIImpl implements ExternalPlayerDAOFootballDataAPI {
    @Override
    public Optional<List<PlayerDraftDTO>> listarJugadores() {
        return Optional.of(List.of());
    }
}
