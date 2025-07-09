package com.asesoria.contable.app_ac.exceptions;

public class AlertaContadorNotFoundException extends RuntimeException {
    public AlertaContadorNotFoundException() {
        super("Alerta de Contador no encontrada");
    }
}
