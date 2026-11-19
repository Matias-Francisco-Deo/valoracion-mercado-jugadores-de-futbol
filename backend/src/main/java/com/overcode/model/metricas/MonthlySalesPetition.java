package com.overcode.model.metricas;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class MonthlySalesPetition {

    private final YearMonth yearMonth;

    public MonthlySalesPetition(Integer year, Integer month) {

        this.yearMonth = YearMonth.of(year, month);
    }

    public LocalDateTime getMinTime() {
        return yearMonth.atDay(1).atStartOfDay();
    }

    public LocalDateTime getMaxTime() {
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
    }

    public Integer getYear() {
        return yearMonth.getYear();
    }

    public Integer getMonth() {
        return yearMonth.getMonth().getValue();
    }
}
