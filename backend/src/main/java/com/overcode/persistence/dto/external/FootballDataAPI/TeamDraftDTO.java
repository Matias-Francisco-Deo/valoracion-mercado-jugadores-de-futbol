package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDraftDTO(Long id, String name, List<FootballDataPlayerDraftDTO> squad) {

}