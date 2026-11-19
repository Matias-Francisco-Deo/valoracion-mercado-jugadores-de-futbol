package com.overcode.service.interfaces;

import com.overcode.model.metricas.MonthlySalesPetition;

public interface SalesMetricsService {

    Long obtenerCantidadVentasMensuales(MonthlySalesPetition petition);
}
