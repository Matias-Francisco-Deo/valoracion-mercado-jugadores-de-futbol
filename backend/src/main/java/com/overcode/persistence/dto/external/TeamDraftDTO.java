package com.overcode.persistence.dto.external;

import com.overcode.model.Team;

public record TeamDraftDTO(String name, String league) {
    public Team aModelo() {
        return new Team(name, league);
    }
}
