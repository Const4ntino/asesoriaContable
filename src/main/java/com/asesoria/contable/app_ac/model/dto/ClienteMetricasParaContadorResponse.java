package com.asesoria.contable.app_ac.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
public class ClienteMetricasParaContadorResponse {
    private Long idCliente;
    private String nombreCliente;

    private YearMonth periodoActual;
    private BigDecimal ingresosMesActual;
    private BigDecimal egresosMesActual;
    private BigDecimal utilidadMesActual;

    private YearMonth periodoAnterior;
    private BigDecimal ingresosMesAnterior;
    private BigDecimal egresosMesAnterior;
    private BigDecimal utilidadMesAnterior;
}
