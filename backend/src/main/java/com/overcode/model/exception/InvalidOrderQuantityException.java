package com.overcode.model.exception;

/**
 * Thrown when an order is created with an invalid quantity (null, zero, or negative).
 */
public class InvalidOrderQuantityException extends RuntimeException {
    public InvalidOrderQuantityException(String message) {
        super(message);
    }
}
