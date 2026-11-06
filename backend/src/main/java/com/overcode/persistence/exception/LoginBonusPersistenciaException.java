package com.overcode.persistence.exception;

public class LoginBonusPersistenciaException extends DiaryBonusPersistenciaException {
    public LoginBonusPersistenciaException(String message) {
        super(message);
    }

    public LoginBonusPersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
