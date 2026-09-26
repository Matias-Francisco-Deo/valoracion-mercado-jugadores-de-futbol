package com.overcode.controller.exception;

import com.overcode.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleConflict(ConflictException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthentication(AuthenticationException ex) {
        return new ErrorResponseDTO(ex.getMessage());
    }
/*
    //este handler devuelve un map con cada campo con su error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
*/
    //este handler devuelve un solo mensaje de error al azar en caso de que haya mas de 1
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult()
                .getFieldErrors()
                .get(0);

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

}
