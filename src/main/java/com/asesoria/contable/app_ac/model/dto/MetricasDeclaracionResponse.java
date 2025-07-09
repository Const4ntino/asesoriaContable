package com.asesoria.contable.app_ac.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricasDeclaracionResponse {

    private BigDecimal ingresosGravados;
    private BigDecimal ingresosExonerados;
    private BigDecimal ingresosInafectos;
    private BigDecimal totalIgvIngresos;

    private BigDecimal egresosGravados;
    private BigDecimal egresosExonerados;
    private BigDecimal egresosInafectos;
    private BigDecimal totalIgvEgresos;
}
