package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.overcode.persistence.dto.external.PlayerDraftDTO;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDraftDTO(Long id, String name, List<FootballDataPlayerDraftDTO> squad) {

}