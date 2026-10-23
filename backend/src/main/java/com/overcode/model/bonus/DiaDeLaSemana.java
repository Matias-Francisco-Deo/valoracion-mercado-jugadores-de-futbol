package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.DiaDeLaSemanaInvalidoException;
import com.overcode.model.bonus.exception.DiaInvalidoException;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public enum DiaDeLaSemana {
    LUNES(1),
    MARTES(2),
    MIERCOLES(3),
    JUEVES(4),
    VIERNES(5),
    SABADO(6),
    DOMINGO(7);

    private final int numeroDia;

    DiaDeLaSemana(int numeroDia) {
        this.numeroDia = numeroDia;
    }

    public static DiaDeLaSemana desdeNumeroDia(int dia) {
        if (dia < 1 || dia > 7) {
            throw new DiaInvalidoException("El día debe estar entre 1 y 7. Valor recibido: " + dia);
        }
        for (DiaDeLaSemana diaVal : values()) {
            if (diaVal.numeroDia == dia) {
                return diaVal;
            }
        }
        throw new DiaInvalidoException("No se encontró día para la dia: " + dia);
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
