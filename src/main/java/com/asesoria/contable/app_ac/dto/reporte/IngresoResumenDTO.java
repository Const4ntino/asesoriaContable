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
public class IngresoResumenDTO {
    private Long id;
    private LocalDate fecha;
    private BigDecimal monto;
    private BigDecimal montoIgv;
    private String descripcion;
    private String nroComprobante;
    private String tipoTributario;
}
