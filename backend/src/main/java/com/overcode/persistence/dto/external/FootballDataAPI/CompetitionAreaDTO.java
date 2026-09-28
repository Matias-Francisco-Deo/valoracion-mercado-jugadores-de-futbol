package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompetitionAreaDTO(String name) {
}
