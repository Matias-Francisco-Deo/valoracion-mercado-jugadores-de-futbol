package com.overcode.service.exception;

public class InsufficientEscrowException extends RuntimeException {
    public InsufficientEscrowException(String message) {
        super(message);
    }
}
