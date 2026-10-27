package com.overcode.controller.exception;

import com.overcode.model.exception.EstrategiaInvalidaException;
import com.overcode.model.exception.InsufficientFundsException;
import com.overcode.model.exception.InvalidOrderQuantityException;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.exception.bonus.LoginBonusInvalidoException;
import com.overcode.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @ExceptionHandler(com.overcode.service.exception.InsufficientEscrowException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleInsufficientEscrow(com.overcode.service.exception.InsufficientEscrowException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(com.overcode.service.exception.OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleOrderNotFound(com.overcode.service.exception.OrderNotFoundException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleIllegalState(IllegalStateException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ErrorResponseDTO handleInsufficientFunds(InsufficientFundsException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        return new ErrorResponseDTO("The order or portfolio was modified by another operation. Please retry.");
    }

    @ExceptionHandler(EstrategiaInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleEstrategiaInvalida(EstrategiaInvalidaException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(RangoBusquedaInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleEstrategiaInvalida(RangoBusquedaInvalidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleInvalidOrderQuantity(InvalidOrderQuantityException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(BonusYaReclamadoHoyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleBonusYaReclamadoHoy(BonusYaReclamadoHoyException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(FechaReclamoBonusLoginInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleFechaReclamoInvalida(FechaReclamoBonusLoginInvalidaException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(LoginBonusInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO loginBonusInvalido(LoginBonusInvalidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ReclamoBonusNoPermitidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleReclamoBonusNoPermitido(ReclamoBonusNoPermitidoException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }
}
