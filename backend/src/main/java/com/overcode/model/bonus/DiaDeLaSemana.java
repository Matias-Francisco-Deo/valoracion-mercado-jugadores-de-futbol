package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.DiaDeLaSemanaInvalidoException;
import com.overcode.model.bonus.exception.RachaInvalidaException;

import java.time.DayOfWeek;

public enum DiaDeLaSemana {
    LUNES(1),
    MARTES(2),
    MIERCOLES(3),
    JUEVES(4),
    VIERNES(5),
    SABADO(6),
    DOMINGO(7);

    private final int numeroRacha;

    DiaDeLaSemana(int numeroRacha) {
        this.numeroRacha = numeroRacha;
    }

    public int getNumeroRacha() {
        return numeroRacha;
    }

    public static DiaDeLaSemana desdeRacha(int racha) {
        if (racha < 1 || racha > 7) {
            throw new RachaInvalidaException("La racha debe estar entre 1 y 7. Valor recibido: " + racha);
        }
        for (DiaDeLaSemana dia : values()) {
            if (dia.numeroRacha == racha) {
                return dia;
            }
        }
        throw new RachaInvalidaException("No se encontró día para la racha: " + racha);
    }

    public static DiaDeLaSemana desdeDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new DiaDeLaSemanaInvalidoException("El día de la semana no puede ser nulo.");
        }
        return switch (dayOfWeek) {
            case MONDAY -> LUNES;
            case TUESDAY -> MARTES;
            case WEDNESDAY -> MIERCOLES;
            case THURSDAY -> JUEVES;
            case FRIDAY -> VIERNES;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
        };
    }
}
