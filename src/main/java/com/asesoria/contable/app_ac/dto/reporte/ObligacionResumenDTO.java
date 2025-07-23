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
public class ObligacionResumenDTO {
    private Long id;
    private String tipo;
    private LocalDate periodoTributario;
    private BigDecimal monto;
    private LocalDate fechaLimite;
    private String estado;
    private String observaciones;
}
