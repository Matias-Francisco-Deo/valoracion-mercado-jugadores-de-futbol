package com.overcode.persistence.exception;

public class LoginBonusPersistenciaException extends RuntimeException {
    public LoginBonusPersistenciaException(String message) {
        super(message);
    }

    public LoginBonusPersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
