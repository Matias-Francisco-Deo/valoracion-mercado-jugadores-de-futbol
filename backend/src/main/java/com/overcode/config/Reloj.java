package com.overcode.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Reloj {

    private Reloj() {

    }

    public static final ZoneOffset TIME_ZONE = ZoneOffset.UTC;

    public static LocalDate getLocalDate() {
        return LocalDate.now(TIME_ZONE);
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now(TIME_ZONE);
    }
}
