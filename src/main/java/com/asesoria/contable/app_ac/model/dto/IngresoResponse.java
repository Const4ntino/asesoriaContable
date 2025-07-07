package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoResponse {

    private Long id;
    private ClienteResponse cliente;
    private BigDecimal monto;
    private BigDecimal montoIgv;
    private LocalDate fecha;
    private String descripcion;
    private String nroComprobante;
    private String urlComprobante;
    private TipoTributario tipoTributario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
