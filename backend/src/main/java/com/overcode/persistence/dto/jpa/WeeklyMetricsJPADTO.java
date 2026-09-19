package com.overcode.persistence.dto.jpa;

import com.overcode.model.WeeklyMetrics;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class WeeklyMetricsJPADTO {
    private Integer goals;
    private Integer assists;
    private Integer shotsOnTarget;
    private Integer passes;
    private Integer interceptions;
    private Integer tackles;
    private Integer keyPasses;
    private Double rating;
    private Integer wasDribbled;
    private Integer successfulDribbles;
    private Integer gamesPlayed;

    public static WeeklyMetricsJPADTO desdeModelo(WeeklyMetrics metrics) {
        if (metrics == null) return null;
        WeeklyMetricsJPADTO dto = new WeeklyMetricsJPADTO();
        dto.setGoals(metrics.getGoals());
        dto.setAssists(metrics.getAssists());
        dto.setShotsOnTarget(metrics.getShotsOnTarget());
        dto.setPasses(metrics.getPasses());
        dto.setInterceptions(metrics.getInterceptions());
        dto.setTackles(metrics.getTackles());
        dto.setKeyPasses(metrics.getKeyPasses());
        dto.setRating(metrics.getRating());
        dto.setWasDribbled(metrics.getWasDribbled());
        dto.setSuccessfulDribbles(metrics.getSuccessfulDribbles());
        dto.setGamesPlayed(metrics.getGamesPlayed());
        return dto;
    }

    public WeeklyMetrics aModelo() {
        WeeklyMetrics metrics = new WeeklyMetrics();
        metrics.setGoals(this.goals);
        metrics.setAssists(this.assists);
        metrics.setShotsOnTarget(this.shotsOnTarget);
        metrics.setPasses(this.passes);
        metrics.setInterceptions(this.interceptions);
        metrics.setTackles(this.tackles);
        metrics.setKeyPasses(this.keyPasses);
        metrics.setRating(this.rating);
        metrics.setWasDribbled(this.wasDribbled);
        metrics.setSuccessfulDribbles(this.successfulDribbles);
        metrics.setGamesPlayed(this.gamesPlayed);
        return metrics;
    }
}
