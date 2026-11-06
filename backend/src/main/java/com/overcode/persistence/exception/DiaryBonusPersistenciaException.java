package com.overcode.persistence.exception;

public class DiaryBonusPersistenciaException extends RuntimeException {
    public DiaryBonusPersistenciaException(String message) {
        super(message);
    }

    public DiaryBonusPersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
