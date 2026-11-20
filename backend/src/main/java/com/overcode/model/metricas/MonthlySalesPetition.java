package com.overcode.model.metricas;

import com.overcode.model.Reloj;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class MonthlySalesPetition {

    private final YearMonth yearMonth;

    public MonthlySalesPetition() {
        this.yearMonth = Reloj.getYearMonth();
    }

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
