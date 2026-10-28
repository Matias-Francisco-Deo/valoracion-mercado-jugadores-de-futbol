package com.overcode.model.exception;

public class InsufficientLiquidityException extends RuntimeException {
    public InsufficientLiquidityException(String message) {
        super(message);
    }
}
