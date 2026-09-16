package com.overcode.controller;

import com.overcode.infrastructure.scraper.adapter.WhoScoredAdapter;
import com.overcode.infrastructure.scraper.exception.ScraperExtractionException;
import com.overcode.persistence.dto.WeeklyMetrics;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/scraper")
@Tag(name = "Scraper", description = "Internal endpoints for scraping data (e.g., used by CRON jobs)")
public class ScraperController {

    private final WhoScoredAdapter whoScoredAdapter;

    public ScraperController(WhoScoredAdapter whoScoredAdapter) {
        this.whoScoredAdapter = whoScoredAdapter;
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> getPlayerMetrics(
            @RequestParam String team,
            @RequestParam String player) {
        try {
            WeeklyMetrics metrics = whoScoredAdapter.getPlayerMetrics(team, player);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "metrics", metrics
            ));
        } catch (ScraperExtractionException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // =========================================================================
    // ENDPOINT TEMPORAL PARA DEBUGGEAR EL JSON CRUDO
    // =========================================================================
    @GetMapping("/debug-json")
    public ResponseEntity<?> getRawJson(@RequestParam Long playerId) {
        try {
            String url = "https://www.whoscored.com/players/" + playerId + "/show/";
            String html = new com.overcode.infrastructure.scraper.http.ScraperHttpClient().getHtml(url);
            String rawJson = com.overcode.infrastructure.scraper.util.JsonExtractorUtil.extractPlayerStatsJson(html);
            return ResponseEntity.ok(rawJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
