package com.overcode.controller.exception;

import com.overcode.model.exception.*;
import com.overcode.service.exception.*;
import com.overcode.service.exception.venta.ExisteOrdenAbiertaParaTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFound(EntidadNoEncontradaException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidation(ValidationException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthentication(AuthenticationException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    //este handler devuelve un solo mensaje de error al azar en caso de que haya mas de 1
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult()
                .getFieldErrors()
                .getFirst();

        return new ErrorResponseDTO(error.getDefaultMessage());
    }

    @ExceptionHandler(NombreRepetidoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleRepeatedName(NombreRepetidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(EmailRepetidoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleRepeatedEmail(EmailRepetidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(TokensInsuficientesParaVenderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleInsufficientEscrow(TokensInsuficientesParaVenderException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleOrderNotFound(OrderNotFoundException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(FondosInsuficientesException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ErrorResponseDTO handleInsufficientFunds(FondosInsuficientesException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(EstrategiaInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleEstrategiaInvalida(EstrategiaInvalidaException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(RangoBusquedaInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleRangoBusquedaInvalido(RangoBusquedaInvalidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(TokensInsuficientesParaComprarException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleInsufficientLiquidity(TokensInsuficientesParaComprarException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleInvalidOrderQuantity(InvalidOrderQuantityException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ExisteOrdenAbiertaParaTokenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleExisteOrdenAbiertaParaToken(ExisteOrdenAbiertaParaTokenException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(RequestNoValidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleRequestNoValida(RequestNoValidaException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ParametroNoValidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleParametroNoValido(ParametroNoValidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }


}
