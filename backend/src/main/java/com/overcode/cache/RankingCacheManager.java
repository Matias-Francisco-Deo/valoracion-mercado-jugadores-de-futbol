package com.overcode.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingCacheManager {

    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 3 * * *",zone = "America/Argentina/Buenos_Aires")
    public void limpiarRankingDiariamente() {
        invalidarRanking();
    }

    private void invalidarRanking() {
        Cache cache = cacheManager.getCache("ranking");

        if (cache != null) {
            cache.clear();
        }
    }
}