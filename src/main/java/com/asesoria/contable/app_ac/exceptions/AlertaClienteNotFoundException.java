package com.asesoria.contable.app_ac.exceptions;

public class AlertaClienteNotFoundException extends RuntimeException {
    public AlertaClienteNotFoundException() {
        super("Alerta de Cliente no encontrada");
    }
}
