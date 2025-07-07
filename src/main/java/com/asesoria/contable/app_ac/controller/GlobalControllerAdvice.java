package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.model.ErrorResponse;
import com.asesoria.contable.app_ac.utils.enums.ErrorCatalog;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.asesoria.contable.app_ac.utils.enums.ErrorCatalog.*;

@RestControllerAdvice
public class GlobalControllerAdvice {

    // Errores específicos primero y luego los generales
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ContadorNotFoundException.class)
    public ErrorResponse handlerTrabajadorNotFoundException() {
        return ErrorResponse.builder()
                .codigo(CONTADOR_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(CONTADOR_NO_ENCONTRADO.getMensaje())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ClienteNotFoundException.class)
    public ErrorResponse handlerClienteNotFoundException() {
        return ErrorResponse.builder()
                .codigo(CLIENTE_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(CLIENTE_NO_ENCONTRADO.getMensaje())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ErrorResponse handlerUsuarioNotFoundException() {
        return ErrorResponse.builder()
                .codigo(USUARIO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(USUARIO_NO_ENCONTRADO.getMensaje())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }


//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(UsuarioNotFoundException.class)
//    public ErrorResponse handlerUsuarioNotFoundException() {
//        return ErrorResponse.builder()
//                .codigo(USUARIO_NO_ENCONTRADO.getCodigo())
//                .status(HttpStatus.NOT_FOUND)
//                .mensaje(USUARIO_NO_ENCONTRADO.getMensaje())
//                .marcaDeTiempo(LocalDateTime.now())
//                .build();
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();

        String nombreObjeto = result.getObjectName();

        ErrorCatalog errorCatalago;

        if (nombreObjeto.toLowerCase().contains("trabajador")) {
            errorCatalago = CONTADOR_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("cliente")) {
            errorCatalago = CLIENTE_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("usuario")) {
            errorCatalago = USUARIO_DATOS_INVALIDOS;
        } else {
            errorCatalago = ERROR_GENERICO;
        }

        return ErrorResponse.builder()
                .codigo(errorCatalago.getCodigo())
                .status(HttpStatus.BAD_REQUEST)
                .mensaje(errorCatalago.getMensaje())
                .detalleMensaje(result.getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList()))
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerInternalServerError(Exception exception) {
        return ErrorResponse.builder()
                .codigo(ERROR_GENERICO.getCodigo())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .mensaje(ERROR_GENERICO.getMensaje())
                .detalleMensaje(Collections.singletonList(exception.getMessage()))
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }
}
