package com.overcode.service.impl;

import com.overcode.model.metricas.MonthlySalesPetition;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import com.overcode.service.interfaces.SalesMetricsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalesMetricsServiceImpl implements SalesMetricsService {

    private final TransactionRepository transactionRepository;

    public SalesMetricsServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Long obtenerCantidadVentasMensuales(MonthlySalesPetition petition) {
        LocalDateTime inicio = petition.getMinTime();
        LocalDateTime fin = petition.getMaxTime();

        return transactionRepository.contarVentasEntre(inicio, fin);
    }
}
