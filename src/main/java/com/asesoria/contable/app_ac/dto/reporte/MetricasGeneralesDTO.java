package com.asesoria.contable.app_ac.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasGeneralesDTO {
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal balance;
    private BigDecimal utilidadEstimada;
    private BigDecimal igvPorPagar;
    private BigDecimal irEstimado;
    private Map<String, BigDecimal> tendenciaMensual; // Últimos 6 meses
}
