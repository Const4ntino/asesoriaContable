package com.asesoria.contable.app_ac.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PagoNotFoundException extends RuntimeException {
    public PagoNotFoundException() {
        super("Pago no encontrado");
    }

    public PagoNotFoundException(String message) {
        super(message);
    }
}
