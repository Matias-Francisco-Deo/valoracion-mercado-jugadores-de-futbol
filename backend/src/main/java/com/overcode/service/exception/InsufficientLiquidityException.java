package com.overcode.service.exception;

public class InsufficientLiquidityException extends RuntimeException {
    public InsufficientLiquidityException(String message) {
        super(message);
    }
}
