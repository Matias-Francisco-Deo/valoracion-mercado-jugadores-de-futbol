package com.overcode.service.impl;

import com.overcode.model.metricas.MonthlySalesPetition;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.service.interfaces.SalesMetricsService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalesMetricsServiceImpl implements SalesMetricsService {

    private final TransactionRepository transactionRepository;


    public SalesMetricsServiceImpl(MeterRegistry meterRegistry, TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;

        MonthlySalesPetition petition = new MonthlySalesPetition();
        addToRegistry(meterRegistry, petition);
    }

    private void addToRegistry(MeterRegistry meterRegistry, MonthlySalesPetition petition) {
        Gauge.builder("monthly_sales", () -> obtenerCantidadVentasMensuales(petition))
                .description("Ventas mensuales de tokens")
                .tags("sales", "sells", "business", "financial")
                .register(meterRegistry);
    }

    @Override
    public Long obtenerCantidadVentasMensuales(MonthlySalesPetition petition) {
        LocalDateTime inicio = petition.getMinTime();
        LocalDateTime fin = petition.getMaxTime();

        return transactionRepository.contarVentasEntre(inicio, fin);
    }
}
