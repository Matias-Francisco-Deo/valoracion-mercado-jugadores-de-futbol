package com.overcode.cache;

import com.overcode.cache.event.RecotizacionFinalizadaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class RankingCacheManager {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alFinalizarRecotizacion(RecotizacionFinalizadaEvent event) {
        invalidarRanking();
    }

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