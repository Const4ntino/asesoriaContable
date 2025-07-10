package com.asesoria.contable.app_ac.utils.enums;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    CONTADOR_NO_ENCONTRADO("ERROR_CONTADOR_01", "Contador no encontrado"),
    CONTADOR_DATOS_INVALIDOS("ERROR_CONTADOR_02", "Datos inválidos para Contador"),

    CLIENTE_NO_ENCONTRADO("ERROR_CLIENTE_01", "Cliente no encontrado"),
    CLIENTE_DATOS_INVALIDOS("ERROR_CLIENTE_02", "Datos inválidos para Cliente"),

    USUARIO_NO_ENCONTRADO("ERROR_USUARIO_01", "Usuario no encontrado"),
    USUARIO_DATOS_INVALIDOS("ERROR_USUARIO_02", "Datos inválidos para Usuario"),

    INGRESO_NOT_FOUND("ERROR_INGRESO_01", "Ingreso no encontrado"),
    INGRESO_DATOS_INVALIDOS("ERROR_INGRESO_02", "Datos inválidos para Ingreso"),

    EGRESO_NOT_FOUND("ERROR_EGRESO_01", "Egreso no encontrado"),
    EGRESO_DATOS_INVALIDOS("ERROR_EGRESO_02", "Datos inválidos para Egreso"),

    DECLARACION_NOT_FOUND("ERROR_DECLARACION_01", "Declaración no encontrada"),
    DECLARACION_DATOS_INVALIDOS("ERROR_DECLARACION_02", "Datos inválidos para Declaracion"),

    OBLIGACION_NOT_FOUND("ERROR_OBLIGACION_01", "Obligación no encontrada"),
    OBLIGACION_DATOS_INVALIDOS("ERROR_OBLIGACION_02", "Datos inválidos para Obligación"),

    ERROR_GENERICO("ERROR_GENERICO", "Un error inesperado a ocurrido");

    private final String codigo;
    private final String mensaje;

    ErrorCatalog(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }
}
