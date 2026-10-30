package com.overcode.controller.exception;

public class RequestNoValidaException extends RuntimeException {
    public RequestNoValidaException(String message) {
        super(message);
    }
}
