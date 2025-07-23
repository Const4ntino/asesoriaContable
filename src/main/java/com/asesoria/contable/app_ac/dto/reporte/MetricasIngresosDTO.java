package com.asesoria.contable.app_ac.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasIngresosDTO {
    private BigDecimal totalMesActual;
    private BigDecimal totalMesAnterior;
    private BigDecimal variacionPorcentual;
    private Map<String, BigDecimal> ingresosPorTipoTributario; // GRAVADA, EXONERADA, INAFECTA
    private List<IngresoResumenDTO> ultimosIngresos;
}
