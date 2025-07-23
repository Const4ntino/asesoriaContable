package com.asesoria.contable.app_ac.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclaracionResumenDTO {
    private Long id;
    private LocalDate periodoTributario;
    private String tipo;
    private String estado;
    private LocalDate fechaLimite;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal utilidadEstimada;
    private BigDecimal igvVentas;
    private BigDecimal igvCompras;
    private BigDecimal irEstimado;
    private BigDecimal totalPagarDeclaracion;
}
