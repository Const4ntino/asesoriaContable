package com.asesoria.contable.app_ac.exceptions;

import com.asesoria.contable.app_ac.utils.enums.ErrorCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IngresoNotFoundException extends RuntimeException {

    public IngresoNotFoundException() {
    }
}
