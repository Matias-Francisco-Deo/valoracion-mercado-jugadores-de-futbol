package com.overcode.persistence.dto;

public class WeeklyMetrics {
    private Long playerId;
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
    /**
     * NOTA ARQUITECTÓNICA:
     * Almacenamos las métricas (tackles, keyPasses, etc.) en su valor TOTAL ABSOLUTO para no perder precisión.
     * Para mostrar el "2.7" (promedio) para que se vea igual que en WhoScored,
     * hay que dividir la métrica por 'gamesPlayed' (Apps = GameStarted + SubOn del JSON)
     */
    private Integer gamesPlayed;

    // Getters and Setters
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Integer getGoals() { return goals; }
    public void setGoals(Integer goals) { this.goals = goals; }

    public Integer getAssists() { return assists; }
    public void setAssists(Integer assists) { this.assists = assists; }

    public Integer getShotsOnTarget() { return shotsOnTarget; }
    public void setShotsOnTarget(Integer shotsOnTarget) { this.shotsOnTarget = shotsOnTarget; }

    public Integer getPasses() { return passes; }
    public void setPasses(Integer passes) { this.passes = passes; }

    public Integer getInterceptions() { return interceptions; }
    public void setInterceptions(Integer interceptions) { this.interceptions = interceptions; }

    public Integer getTackles() { return tackles; }
    public void setTackles(Integer tackles) { this.tackles = tackles; }

    public Integer getKeyPasses() { return keyPasses; }
    public void setKeyPasses(Integer keyPasses) { this.keyPasses = keyPasses; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getWasDribbled() { return wasDribbled; }
    public void setWasDribbled(Integer wasDribbled) { this.wasDribbled = wasDribbled; }

    public Integer getSuccessfulDribbles() { return successfulDribbles; }
    public void setSuccessfulDribbles(Integer successfulDribbles) { this.successfulDribbles = successfulDribbles; }

    public Integer getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(Integer gamesPlayed) { this.gamesPlayed = gamesPlayed; }
}
