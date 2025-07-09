package com.asesoria.contable.app_ac.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteConMetricasResponse {
    // Datos del Cliente
//    private Long id;
//    private String nombres;
//    private String apellidos;
//    private String rucDni;
//    private String regimen;
    private ClienteResponse cliente;

    // Métricas del Mes Actual
    private BigDecimal totalIngresosMesActual;
    private BigDecimal totalEgresosMesActual;
    private BigDecimal utilidadMesActual;
}
