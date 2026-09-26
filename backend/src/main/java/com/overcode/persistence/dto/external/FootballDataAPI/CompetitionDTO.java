package com.overcode.persistence.dto.external.FootballDataAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record CompetitionDTO(Long id, String name, CompetitionAreaDTO area) {
    public String getCountry() {
        return area.name();
    }
}