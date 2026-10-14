package com.overcode.model.exception;

/** Thrown by the domain when a portfolio does not hold enough credits for an operation. */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
