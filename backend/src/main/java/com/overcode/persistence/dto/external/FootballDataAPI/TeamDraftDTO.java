package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDraftDTO(Long id, String name) {

}