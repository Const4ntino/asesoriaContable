package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class IngresoRequest {

    @NotNull
    private Long idCliente;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal monto;

    //@DecimalMin(value = "0.0", inclusive = false)
    @NotNull
    private BigDecimal montoIgv;

    @NotNull
    private LocalDate fecha;

    private String descripcion;

    private String nroComprobante;

    private String urlComprobante;

    private TipoTributario tipoTributario;
}
