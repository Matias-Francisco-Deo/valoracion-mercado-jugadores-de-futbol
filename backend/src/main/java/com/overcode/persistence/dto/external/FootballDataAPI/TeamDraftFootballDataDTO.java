package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.overcode.persistence.dto.external.TeamDraftDTO;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDraftFootballDataDTO(Long id, String name, List<FootballDataPlayerDraftDTO> squad) {
    public TeamDraftDTO toTeamDraftDTO(String league) {
        return new TeamDraftDTO(name, league);
    }
}