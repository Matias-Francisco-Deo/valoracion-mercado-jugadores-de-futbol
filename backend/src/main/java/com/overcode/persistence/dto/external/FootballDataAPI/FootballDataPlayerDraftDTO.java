package com.overcode.persistence.dto.external.FootballDataAPI;

import com.overcode.persistence.dto.external.PlayerDraftDTO;

public record FootballDataPlayerDraftDTO(Long id, String name) {
    public PlayerDraftDTO toPlayerDraftDTO() {
        return new PlayerDraftDTO(name);
    }
}
