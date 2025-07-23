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
public class PagoResumenDTO {
    private Long id;
    private Long obligacionId;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String medioPago;
    private String estado;
    private String pagadoPor;
    private String comentarioContador;
}
