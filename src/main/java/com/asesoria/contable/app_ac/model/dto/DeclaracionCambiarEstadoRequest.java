package com.asesoria.contable.app_ac.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeclaracionCambiarEstadoRequest {
    private String urlConstancia;
    private BigDecimal monto;
}
