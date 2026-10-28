package com.overcode.model.exception;

public class InsufficientEscrowException extends RuntimeException {
    public InsufficientEscrowException(String message) {
        super(message);
    }
}
