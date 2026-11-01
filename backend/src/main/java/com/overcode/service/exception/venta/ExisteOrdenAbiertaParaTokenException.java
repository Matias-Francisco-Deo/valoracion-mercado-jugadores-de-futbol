package com.overcode.service.exception.venta;

public class ExisteOrdenAbiertaParaTokenException extends RuntimeException {
    public ExisteOrdenAbiertaParaTokenException(String message) {
        super(message);
    }
}