package com.asesoria.contable.app_ac.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteConMetricasResponse {
    private ClienteResponse cliente;

    // Métricas del Mes Actual
    private BigDecimal totalIngresosMesActual;
    private BigDecimal totalEgresosMesActual;
    private BigDecimal utilidadMesActual;

    // Métricas del Mes Anterior

}
